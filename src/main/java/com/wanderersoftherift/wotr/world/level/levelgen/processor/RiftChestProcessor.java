package com.wanderersoftherift.wotr.world.level.levelgen.processor;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.block.RiftChestEntityBlock;
import com.wanderersoftherift.wotr.init.WotrBlocks;
import com.wanderersoftherift.wotr.init.worldgen.WotrProcessors;
import com.wanderersoftherift.wotr.item.RiftChestType;
import com.wanderersoftherift.wotr.util.RandomUtil;
import com.wanderersoftherift.wotr.world.level.levelgen.RiftProcessedRoom;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.output.DefaultOutputBlockState;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.output.OutputBlockState;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.util.ProcessorUtil;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.util.StructureRandomType;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.util.WeightedRiftChestTypeEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.Weight;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.wanderersoftherift.wotr.init.WotrBlocks.CHEST_TYPES;
import static com.wanderersoftherift.wotr.world.level.levelgen.processor.util.StructureRandomType.RANDOM_TYPE_CODEC;
import static net.minecraft.world.level.block.Blocks.AIR;
import static net.minecraft.world.level.block.Blocks.CHEST;

public class RiftChestProcessor extends StructureProcessor implements RiftFinalProcessor {

    public static final MapCodec<RiftChestProcessor> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            ResourceLocation.CODEC.optionalFieldOf("base_loot_table", ResourceLocation.parse("wotr:empty"))
                    .forGetter(RiftChestProcessor::getBaseLootTable),
            OutputBlockState.DIRECT_CODEC.optionalFieldOf("replace_removed_with", new DefaultOutputBlockState(AIR))
                    .forGetter(RiftChestProcessor::getReplaceOutput),
            IntProvider.CODEC.optionalFieldOf("count", ConstantInt.of(1)).forGetter(RiftChestProcessor::getCount),
            WeightedRiftChestTypeEntry.CODEC.listOf()
                    .fieldOf("chest_types")
                    .forGetter(RiftChestProcessor::getChestTypes),
            RANDOM_TYPE_CODEC.optionalFieldOf("random_type", StructureRandomType.BLOCK)
                    .forGetter(RiftChestProcessor::getRandomType)
    ).apply(builder, builder.stable(RiftChestProcessor::new)));

    private static final long SEED = 2465482L;

    private final ResourceLocation baseLootTable;
    private final OutputBlockState replaceOutput;
    private final IntProvider count;
    private final List<WeightedRiftChestTypeEntry> chestTypes;
    private final StructureRandomType randomType;
    private final PositionalRandomFactory randomFactory;

    private final List<ResourceKey<LootTable>> lootTableCache;

    public RiftChestProcessor(ResourceLocation baseLootTable, OutputBlockState replaceOutput, IntProvider count,
            List<WeightedRiftChestTypeEntry> chestTypes, StructureRandomType randomType) {
        this.baseLootTable = baseLootTable;
        this.replaceOutput = replaceOutput;
        this.count = count;
        this.chestTypes = chestTypes;
        this.randomType = randomType;
        this.randomFactory = new LegacyRandomSource.LegacyPositionalRandomFactory(SEED);
        lootTableCache = Arrays.stream(RiftChestType.values()).map(this::getLootTable).toList();
    }

    @Override
    public void finalizeRoomProcessing(
            RiftProcessedRoom room,
            ServerLevelAccessor world,
            BlockPos structurePos,
            Vec3i pieceSize) {
        List<BlockEntity> chests = room.getBlockEntities()
                .filter(entity -> entity instanceof RandomizableContainerBlockEntity
                        && isInRoom(entity.getBlockPos(), structurePos, pieceSize)
                        && isChest(room.getBlock(entity.getBlockPos())))
                .collect(Collectors.toList());

        RandomSource random = ProcessorUtil.getRandom(randomType, structurePos, structurePos, BlockPos.ZERO, world,
                SEED);
        int actualCount = count.sample(random);
        RandomUtil.randomSplit(chests, actualCount, random);

        for (int i = actualCount; i < chests.size(); i++) {
            BlockEntity entity = chests.get(i);
            room.setBlock(entity.getBlockPos(), replaceOutput.convertBlockState());
            room.removeBlockEntity(entity.getBlockPos());
        }
        for (int i = 0; i < actualCount && i < chests.size(); i++) {
            BlockEntity originalEntity = chests.get(i);
            BlockPos pos = originalEntity.getBlockPos();
            BlockState inState = room.getBlock(pos);
            BlockState outState = inState;
            ChestType type = inState.getValue(ChestBlock.TYPE);
            // TODO: Will need to review this logic in the presence of multiple rift chest types, if they are randomised
            // here.
            if (type != ChestType.SINGLE
                    && !isChest(room.getBlock(pos.relative(ChestBlock.getConnectedDirection(outState))))) {
                outState = outState.setValue(ChestBlock.TYPE, ChestType.SINGLE);
            }

            RiftChestType replaceType = getRandomChestType(random);
            RiftChestEntityBlock newChestBlock = (RiftChestEntityBlock) CHEST_TYPES.get(replaceType).get();
            outState = ProcessorUtil.copyState(newChestBlock.defaultBlockState(), outState);

            BlockEntity entity = originalEntity;
            if (inState.is(CHEST)) {
                entity = newChestBlock.newBlockEntity(pos, outState);
                entity.loadWithComponents(originalEntity.saveWithoutMetadata(world.registryAccess()),
                        world.registryAccess());
                room.setBlockEntity(entity);
            }
            if (entity instanceof RandomizableContainerBlockEntity container) {
                container.setLootTable(lootTableCache.get(replaceType.ordinal()), random.nextLong());
            }
            room.setBlock(pos, outState);
        }
    }

    private static boolean isInRoom(BlockPos pos, BlockPos structurePos, Vec3i pieceSize) {
        return pos.getX() >= structurePos.getX() && pos.getY() >= structurePos.getY()
                && pos.getZ() >= structurePos.getZ() && pos.getX() < structurePos.getX() + pieceSize.getX()
                && pos.getY() < structurePos.getY() + pieceSize.getY()
                && pos.getZ() < structurePos.getZ() + pieceSize.getZ();
    }

    private boolean isChest(BlockState state) {
        return state != null && (state.is(WotrBlocks.RIFT_CHEST) || state.is(Blocks.CHEST)) && state.hasBlockEntity();
    }

    private RiftChestType getRandomChestType(RandomSource random) {
        return WeightedRandom.getRandomItem(random, chestTypes)
                .orElse(new WeightedRiftChestTypeEntry(RiftChestType.WOODEN, Weight.of(1)))
                .getRiftChestType();
    }

    private @NotNull ResourceKey<LootTable> getLootTable(RiftChestType chestType) {
        return ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath(
                this.baseLootTable.getNamespace(), this.baseLootTable.getPath() + chestType.name().toLowerCase()));
    }

    @Override
    protected @NotNull StructureProcessorType<?> getType() {
        return WotrProcessors.RIFT_CHESTS.get();
    }

    public ResourceLocation getBaseLootTable() {
        return baseLootTable;
    }

    public OutputBlockState getReplaceOutput() {
        return replaceOutput;
    }

    public IntProvider getCount() {
        return count;
    }

    public List<WeightedRiftChestTypeEntry> getChestTypes() {
        return chestTypes;
    }

    public StructureRandomType getRandomType() {
        return randomType;
    }

    public PositionalRandomFactory getRandomFactory() {
        return randomFactory;
    }
}