package com.wanderersoftherift.wotr.world.level.levelgen.processor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.block.RiftChestEntityBlock;
import com.wanderersoftherift.wotr.item.RiftChestType;
import com.wanderersoftherift.wotr.util.Ref;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.output.DefaultOutputBlockState;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.output.OutputBlockState;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.util.ProcessorUtil;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.util.StructureRandomType;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.util.WeightedRiftChestTypeEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.Weight;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

import static com.wanderersoftherift.wotr.init.WotrBlocks.CHEST_TYPES;
import static com.wanderersoftherift.wotr.init.WotrBlocks.RIFT_CHEST;
import static com.wanderersoftherift.wotr.init.worldgen.WotrProcessors.RIFT_CHESTS;
import static com.wanderersoftherift.wotr.world.level.levelgen.processor.util.StructureRandomType.RANDOM_TYPE_CODEC;
import static net.minecraft.world.level.block.Blocks.AIR;
import static net.minecraft.world.level.block.Blocks.CHEST;
import static net.minecraft.world.level.block.ChestBlock.FACING;

public class RiftChestProcessor extends StructureProcessor implements RiftTemplateProcessor {

    public static final MapCodec<RiftChestProcessor> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            ResourceLocation.CODEC.optionalFieldOf("base_loot_table", ResourceLocation.parse("wotr:empty"))
                    .forGetter(RiftChestProcessor::getBaseLootTable),
            OutputBlockState.DIRECT_CODEC.optionalFieldOf("output_state", new DefaultOutputBlockState(AIR))
                    .forGetter(RiftChestProcessor::getReplaceOutput),
            Codec.FLOAT.optionalFieldOf("rarity", 1.0F).forGetter(RiftChestProcessor::getRarity),
            WeightedRiftChestTypeEntry.CODEC.listOf()
                    .fieldOf("chest_types")
                    .forGetter(RiftChestProcessor::getChestTypes),
            RANDOM_TYPE_CODEC.optionalFieldOf("random_type", StructureRandomType.BLOCK)
                    .forGetter(RiftChestProcessor::getRandomType)
    ).apply(builder, builder.stable(RiftChestProcessor::new)));
    private static final long SEED = 2465482L;

    private final ResourceLocation baseLootTable;
    private final OutputBlockState replaceOutput;
    private final float rarity;
    private final List<WeightedRiftChestTypeEntry> chestTypes;
    private final StructureRandomType randomType;
    private final PositionalRandomFactory randomFactory;

    private final List<ResourceKey<LootTable>> lootTableCache;

    public RiftChestProcessor(ResourceLocation baseLootTable, OutputBlockState replaceOutput, float rarity,
            List<WeightedRiftChestTypeEntry> chestTypes, StructureRandomType randomType) {
        this.baseLootTable = baseLootTable;
        this.replaceOutput = replaceOutput;
        this.rarity = rarity;
        this.chestTypes = chestTypes;
        this.randomType = randomType;
        this.randomFactory = new LegacyRandomSource.LegacyPositionalRandomFactory(SEED);
        lootTableCache = Arrays.stream(RiftChestType.values()).map(type -> getLootTable(type)).toList();
    }

    @Override
    public StructureTemplate.StructureBlockInfo process(
            LevelReader world,
            BlockPos piecePos,
            BlockPos structurePos,
            StructureTemplate.StructureBlockInfo rawBlockInfo,
            StructureTemplate.StructureBlockInfo blockInfo,
            StructurePlaceSettings settings,
            @Nullable StructureTemplate template) {
        if ((blockInfo.state().is(RIFT_CHEST.get()) || blockInfo.state().is(CHEST))
                && blockInfo.state().hasBlockEntity()) {
            RandomSource random;
            BlockPos pos = blockInfo.pos();
            random = ProcessorUtil.getRandom(randomType, pos, piecePos, structurePos, world, SEED);
            if (random.nextFloat() < rarity) {
                RiftChestType chestType = getRandomChestType(random);
                BlockState blockState = CHEST_TYPES.get(chestType).get().defaultBlockState();
                blockState = copyProperties(blockState, blockInfo.state());
                BlockEntity tileEntity = ((RiftChestEntityBlock) blockState.getBlock()).newBlockEntity(pos, blockState);
                tileEntity.loadWithComponents(blockInfo.nbt(), world.registryAccess());
                ((RandomizableContainerBlockEntity) tileEntity).setLootTable(getLootTable(chestType),
                        /* serverWorld.random.nextLong() */random.nextLong());
                // }
                return new StructureTemplate.StructureBlockInfo(pos, blockState,
                        tileEntity.saveWithId(world.registryAccess()));
            } else {
                BlockState blockState = replaceOutput.convertBlockState();
                return new StructureTemplate.StructureBlockInfo(pos, blockState, null);
            }
        }
        return blockInfo;
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

    private BlockState copyProperties(BlockState blockState, BlockState state) {
        return blockState.setValue(FACING, state.getValue(FACING)); // shouldn't we use ProcessorUtil.copyState()
                                                                    // instead?
    }

    public ResourceLocation getBaseLootTable() {
        return baseLootTable;
    }

    public OutputBlockState getReplaceOutput() {
        return replaceOutput;
    }

    public float getRarity() {
        return rarity;
    }

    public List<WeightedRiftChestTypeEntry> getChestTypes() {
        return chestTypes;
    }

    public StructureRandomType getRandomType() {
        return randomType;
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return RIFT_CHESTS.get();
    }

    @Override
    public BlockState processBlockState(
            BlockState currentState,
            int x,
            int y,
            int z,
            ServerLevelAccessor world,
            BlockPos structurePos,
            Ref<BlockEntity> entityRef,
            boolean isVisible) {

        if (currentState.is(RIFT_CHEST.get()) && currentState.hasBlockEntity()) {
            BlockPos pos = new BlockPos(x, y, z);
            RandomSource random = ProcessorUtil.getRandom(randomType, pos, structurePos, BlockPos.ZERO, world, SEED);
            if (random.nextFloat() < rarity) {
                RiftChestType chestType = getRandomChestType(random);
                BlockState blockState = CHEST_TYPES.get(chestType).get().defaultBlockState();
                blockState = copyProperties(blockState, currentState);
                BlockEntity tileEntity = entityRef.getValue();
                if (tileEntity instanceof RandomizableContainerBlockEntity container) {
                    container.setLootTable(lootTableCache.get(chestType.ordinal()), random.nextLong());
                }
                return blockState;
            } else {
                BlockState blockState = replaceOutput.convertBlockState();

                entityRef.setValue(null);
                return blockState;
            }
        }
        if (currentState.is(CHEST) && currentState.hasBlockEntity()) {
            BlockPos pos = new BlockPos(x, y, z);
            RandomSource random = ProcessorUtil.getRandom(randomType, pos, structurePos, BlockPos.ZERO, world, SEED);
            if (random.nextFloat() < rarity) {
                RiftChestType chestType = getRandomChestType(random);
                var block = (RiftChestEntityBlock) CHEST_TYPES.get(chestType).get();
                BlockState blockState = block.defaultBlockState();
                blockState = copyProperties(blockState, currentState);
                BlockEntity oldTileEntity = entityRef.getValue();
                BlockEntity tileEntity = block.newBlockEntity(pos, blockState);
                tileEntity.loadWithComponents(oldTileEntity.saveWithoutMetadata(world.registryAccess()),
                        world.registryAccess());
                if (tileEntity instanceof RandomizableContainerBlockEntity container) {
                    container.setLootTable(lootTableCache.get(chestType.ordinal()), random.nextLong());
                }
                entityRef.setValue(tileEntity);
                return blockState;
            } else {
                BlockState blockState = replaceOutput.convertBlockState();

                entityRef.setValue(null);
                return blockState;
            }
        }
        return currentState;
    }
}