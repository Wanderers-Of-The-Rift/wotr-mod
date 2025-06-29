package com.wanderersoftherift.wotr.world.level.levelgen.processor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.init.worldgen.WotrProcessors;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.util.ProcessorUtil;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.util.StructureRandomType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.wanderersoftherift.wotr.world.level.levelgen.processor.util.ProcessorUtil.getRandomBlockFromItemTag;
import static com.wanderersoftherift.wotr.world.level.levelgen.processor.util.ProcessorUtil.isFaceFullFast;
import static com.wanderersoftherift.wotr.world.level.levelgen.processor.util.StructureRandomType.BLOCK;
import static com.wanderersoftherift.wotr.world.level.levelgen.processor.util.StructureRandomType.RANDOM_TYPE_CODEC;
import static net.neoforged.neoforge.common.Tags.Items.MUSHROOMS;

public class MushroomProcessor extends StructureProcessor
        implements RiftAdjacencyProcessor<MushroomProcessor.ReplacementData> {
    public static final MapCodec<MushroomProcessor> CODEC = RecordCodecBuilder
            .mapCodec(builder -> builder
                    .group(BuiltInRegistries.BLOCK.byNameCodec()
                            .listOf()
                            .optionalFieldOf("exclusion_list", Collections.emptyList())
                            .forGetter(MushroomProcessor::getExclusionList),
                            Codec.FLOAT.fieldOf("rarity").forGetter(MushroomProcessor::getRarity),
                            RANDOM_TYPE_CODEC.optionalFieldOf("random_type", StructureRandomType.BLOCK)
                                    .forGetter(MushroomProcessor::getStructureRandomType),
                            RANDOM_TYPE_CODEC.optionalFieldOf("tag_random_type", StructureRandomType.PIECE)
                                    .forGetter(MushroomProcessor::getTagStructureRandomType))
                    .apply(builder, MushroomProcessor::new));
    private static final Optional<Long> SEED = Optional.empty();

    private final TagKey<Item> itemTag = MUSHROOMS;
    private final List<Block> exclusionList;
    private final float rarity;
    private final StructureRandomType structureRandomType;
    private final StructureRandomType tagStructureRandomType;

    public MushroomProcessor(List<Block> exclusionList, float rarity, StructureRandomType structureRandomType,
            StructureRandomType tagStructureRandomType) {
        this.exclusionList = exclusionList;
        this.rarity = rarity;
        this.structureRandomType = structureRandomType;
        this.tagStructureRandomType = tagStructureRandomType;
    }

    @Override
    public @NotNull List<StructureTemplate.StructureBlockInfo> finalizeProcessing(
            ServerLevelAccessor serverLevel,
            BlockPos offset,
            BlockPos pos,
            List<StructureTemplate.StructureBlockInfo> originalBlockInfos,
            List<StructureTemplate.StructureBlockInfo> processedBlockInfos,
            StructurePlaceSettings settings) {
        return RiftAdjacencyProcessor.backportFinalizeProcessing(this, serverLevel, offset, pos, originalBlockInfos,
                processedBlockInfos, settings);
    }

    protected StructureProcessorType<?> getType() {
        return WotrProcessors.MUSHROOMS.get();
    }

    public List<Block> getExclusionList() {
        return exclusionList;
    }

    public float getRarity() {
        return rarity;
    }

    public StructureRandomType getStructureRandomType() {
        return structureRandomType;
    }

    public StructureRandomType getTagStructureRandomType() {
        return tagStructureRandomType;
    }

    @Override
    public int processAdjacency(ReplacementData data, BlockState[] adjacentBlocks, boolean isHidden) {
        var old = adjacentBlocks[6];
        if (!isHidden && !old.isAir()) {
            var up = adjacentBlocks[1];
            boolean validUp = (up == null || up.isAir()) && data.recalculateChance() <= rarity
                    && isFaceFullFast(old, BlockPos.ZERO, Direction.UP);
            if (validUp) {
                adjacentBlocks[1] = data.recalculateBlock().defaultBlockState();
                return 2;
            }
        }
        return 0;
    }

    @Override
    public ReplacementData createData(BlockPos structurePos, Vec3i pieceSize, ServerLevelAccessor world) {
        return new ReplacementData(
                itemTag, exclusionList,
                ProcessorUtil.getRiftRandomFactory(world, SEED.orElse(33125144131546418L))
                        .at(structurePos.getX(), structurePos.getY(), structurePos.getZ()),
                ProcessorUtil.getRiftRandomFactory(world, SEED.orElse(513184169416484L))
                        .at(structurePos.getX(), structurePos.getY(), structurePos.getZ()),
                structureRandomType == BLOCK, tagStructureRandomType == BLOCK
        );
    }

    public static class ReplacementData {
        private final TagKey<Item> tagKey;
        private final List<Block> exclusionList;
        private final RandomSource rng1;
        private final RandomSource rng2;
        private final boolean isRng1PerBlock;
        private final boolean isRng2PerBlock;
        private float roll1;
        private Block roll2;

        public ReplacementData(TagKey<Item> tagKey, List<Block> exclusionList, RandomSource rng1, RandomSource rng2,
                boolean isRng1PerBlock, boolean isRng2PerBlock) {
            this.tagKey = tagKey;
            this.exclusionList = exclusionList;
            this.rng1 = rng1;
            this.rng2 = rng2;
            this.isRng1PerBlock = isRng1PerBlock;
            this.isRng2PerBlock = isRng2PerBlock;
            roll1 = rng1.nextFloat();
            roll2 = getRandomBlockFromItemTag(tagKey, rng2, exclusionList);
        }

        public float recalculateChance() {
            if (isRng1PerBlock) {
                roll1 = rng1.nextFloat();
            }
            return roll1;
        }

        public Block recalculateBlock() {
            if (isRng2PerBlock) {
                roll2 = getRandomBlockFromItemTag(tagKey, rng2, exclusionList);
            }
            return roll2;
        }
    }
}
