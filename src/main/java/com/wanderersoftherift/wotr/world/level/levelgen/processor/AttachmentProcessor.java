package com.wanderersoftherift.wotr.world.level.levelgen.processor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.codec.OutputStateCodecs;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.util.ProcessorUtil;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.util.StructureRandomType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;
import java.util.Optional;

import static com.wanderersoftherift.wotr.init.worldgen.WotrProcessors.ATTACHMENT;
import static com.wanderersoftherift.wotr.world.level.levelgen.processor.util.ProcessorUtil.isFaceFullFast;
import static com.wanderersoftherift.wotr.world.level.levelgen.processor.util.ProcessorUtil.shapeForFaceFullCheck;
import static com.wanderersoftherift.wotr.world.level.levelgen.processor.util.StructureRandomType.BLOCK;
import static com.wanderersoftherift.wotr.world.level.levelgen.processor.util.StructureRandomType.RANDOM_TYPE_CODEC;

public class AttachmentProcessor extends StructureProcessor
        implements RiftAdjacencyProcessor<AttachmentProcessor.ReplacementData> {
    public static final MapCodec<AttachmentProcessor> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            OutputStateCodecs.OUTPUT_STATE_CODEC.fieldOf("blockstate").forGetter(AttachmentProcessor::getBlockState),
            Codec.INT.optionalFieldOf("requires_sides", 0).forGetter(AttachmentProcessor::getRequiresSides),
            Codec.BOOL.optionalFieldOf("requires_up", false).forGetter(AttachmentProcessor::isRequiresUp),
            Codec.BOOL.optionalFieldOf("requires_down", false).forGetter(AttachmentProcessor::isRequiresDown),
            Codec.FLOAT.fieldOf("rarity").forGetter(AttachmentProcessor::getRarity),
            RANDOM_TYPE_CODEC.optionalFieldOf("random_type", StructureRandomType.BLOCK)
                    .forGetter(AttachmentProcessor::getStructureRandomType),
            Codec.LONG.optionalFieldOf("seed").forGetter(AttachmentProcessor::getSeed)
    ).apply(builder, AttachmentProcessor::new));
    private static final List<Direction> HORIZONTAL = Direction.Plane.HORIZONTAL.stream().toList();

    private final BlockState blockState;
    private final int requiresSides;
    private final boolean requiresUp;
    private final boolean requiresDown;
    private final float rarity;
    private final StructureRandomType structureRandomType;
    private final Optional<Long> seed;
    private final boolean useOldProcessor;

    public AttachmentProcessor(BlockState blockState, int requiresSides, boolean requiresUp, boolean requiresDown,
            float rarity, StructureRandomType structureRandomType, Optional<Long> seed) {
        this.blockState = blockState;
        this.requiresSides = requiresSides;
        this.requiresUp = requiresUp;
        this.requiresDown = requiresDown;
        this.rarity = rarity;
        this.structureRandomType = structureRandomType;
        this.seed = seed;

        var totalSides = requiresSides;
        if (requiresDown) {
            totalSides++;
        }
        if (requiresUp) {
            totalSides++;
        }
        useOldProcessor = totalSides > 1;
    }

    @Override
    public List<StructureTemplate.StructureBlockInfo> finalizeProcessing(
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
        return ATTACHMENT.get();
    }

    public BlockState getBlockState() {
        return blockState;
    }

    public int getRequiresSides() {
        return requiresSides;
    }

    public boolean isRequiresUp() {
        return requiresUp;
    }

    public boolean isRequiresDown() {
        return requiresDown;
    }

    public float getRarity() {
        return rarity;
    }

    public StructureRandomType getStructureRandomType() {
        return structureRandomType;
    }

    public Optional<Long> getSeed() {
        return seed;
    }

    @Override
    public int processAdjacency(ReplacementData data, BlockState[] adjacentBlocks, boolean isHidden) {
        var old = adjacentBlocks[6];
        var result = 0;
        if (useOldProcessor) {
            if (old.isAir() && data.recalculateChance() <= rarity) {
                int sideCount = requiresSides;

                if (requiresDown) {
                    var block = adjacentBlocks[0];
                    if (block != null && !isFaceFullFast(block, BlockPos.ZERO, Direction.UP)) {
                        return 0;
                    }
                }
                if (requiresUp) {
                    var block = adjacentBlocks[1];
                    if (block != null && !isFaceFullFast(block, BlockPos.ZERO, Direction.DOWN)) {
                        return 0;
                    }
                }

                for (int i = 0; i < HORIZONTAL.size() && sideCount > 0; i++) {
                    var side = HORIZONTAL.get(i);
                    var directionBlock = adjacentBlocks[side.ordinal()];
                    if (directionBlock != null && isFaceFullFast(directionBlock, BlockPos.ZERO, side.getOpposite())) {
                        sideCount--;
                    }
                }

                if (sideCount > 0) {
                    return 0;
                }

                adjacentBlocks[6] = blockState;
                return 0b1000000;
            }
            return 0;
        }
        if (!isHidden && !old.isAir()) {
            VoxelShape shape = null;
            if (requiresUp) {
                var block = adjacentBlocks[0];
                if ((block != null && block.isAir()) && data.recalculateChance() <= rarity) {
                    if (shape == null) {
                        shape = shapeForFaceFullCheck(old, BlockPos.ZERO);
                    }
                    if (isFaceFullFast(shape, Direction.DOWN)) {
                        adjacentBlocks[0] = blockState;
                        result |= 1;
                    }
                }
            }
            if (requiresDown) {
                var block = adjacentBlocks[1];
                if ((block != null && block.isAir()) && data.recalculateChance() <= rarity) {
                    if (shape == null) {
                        shape = shapeForFaceFullCheck(old, BlockPos.ZERO);
                    }
                    if (isFaceFullFast(shape, Direction.UP)) {
                        adjacentBlocks[1] = blockState;
                        result |= 2;
                    }
                }
            }
            if (requiresSides <= 0) {
                return result;
            }
            for (int i = 0; i < HORIZONTAL.size(); i++) {
                var side = HORIZONTAL.get(i);
                var ordinal = side.ordinal();
                var directionBlock = adjacentBlocks[ordinal];
                if ((directionBlock == null || !directionBlock.isAir()) || !(data.recalculateChance() <= rarity)) {
                    continue;
                }
                if (shape == null) {
                    shape = shapeForFaceFullCheck(old, BlockPos.ZERO);
                }
                if (isFaceFullFast(shape, side)) {
                    adjacentBlocks[ordinal] = blockState;
                    result |= 1 << ordinal;
                }
            }
        }
        return result;
    }

    @Override
    public AttachmentProcessor.ReplacementData createData(
            BlockPos structurePos,
            Vec3i pieceSize,
            ServerLevelAccessor world) {
        return new AttachmentProcessor.ReplacementData(
                ProcessorUtil.getRiftRandomFactory(world, seed.orElse(94513147161L))
                        .at(structurePos.getX(), structurePos.getY(), structurePos.getZ()),
                structureRandomType == BLOCK
        );
    }

    public static class ReplacementData {
        private final RandomSource rng1;
        private final boolean isRng1PerBlock;
        private float roll1;

        public ReplacementData(RandomSource rng1, boolean isRng1PerBlock) {
            this.rng1 = rng1;
            this.isRng1PerBlock = isRng1PerBlock;
            roll1 = rng1.nextFloat();
        }

        public float recalculateChance() {
            if (isRng1PerBlock) {
                roll1 = rng1.nextFloat();
            }
            return roll1;
        }
    }

}