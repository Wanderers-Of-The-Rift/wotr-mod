package com.wanderersoftherift.wotr.world.level.levelgen.processor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.codec.OutputStateCodecs;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.util.ProcessorUtil;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.util.StructureRandomType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.wanderersoftherift.wotr.init.ModProcessors.ATTACHMENT;
import static com.wanderersoftherift.wotr.world.level.levelgen.processor.util.ProcessorUtil.getBlockInfo;
import static com.wanderersoftherift.wotr.world.level.levelgen.processor.util.ProcessorUtil.isFaceFull;
import static com.wanderersoftherift.wotr.world.level.levelgen.processor.util.StructureRandomType.RANDOM_TYPE_CODEC;
import static net.minecraft.core.Direction.Plane;

public class AttachmentProcessor extends StructureProcessor {
    public static final MapCodec<AttachmentProcessor> CODEC = RecordCodecBuilder.mapCodec(builder -> builder
            .group(OutputStateCodecs.OUTPUT_STATE_CODEC.fieldOf("blockstate")
                    .forGetter(AttachmentProcessor::getBlockState),
                    Codec.INT.optionalFieldOf("requires_sides", 0).forGetter(AttachmentProcessor::getRequiresSides),
                    Codec.BOOL.optionalFieldOf("requires_up", false).forGetter(AttachmentProcessor::isRequiresUp),
                    Codec.BOOL.optionalFieldOf("requires_down", false).forGetter(AttachmentProcessor::isRequiresDown),
                    Codec.FLOAT.fieldOf("rarity").forGetter(AttachmentProcessor::getRarity),
                    RANDOM_TYPE_CODEC.optionalFieldOf("random_type", StructureRandomType.BLOCK)
                            .forGetter(AttachmentProcessor::getStructureRandomType),
                    Codec.LONG.optionalFieldOf("seed").forGetter(AttachmentProcessor::getSeed))
            .apply(builder, AttachmentProcessor::new));

    private final BlockState blockState;
    private final int requiresSides;
    private final boolean requiresUp;
    private final boolean requiresDown;
    private final float rarity;
    private final StructureRandomType structureRandomType;
    private final Optional<Long> seed;

    public AttachmentProcessor(BlockState blockState, int requiresSides, boolean requiresUp, boolean requiresDown,
            float rarity, StructureRandomType structureRandomType, Optional<Long> seed) {
        this.blockState = blockState;
        this.requiresSides = requiresSides;
        this.requiresUp = requiresUp;
        this.requiresDown = requiresDown;
        this.rarity = rarity;
        this.structureRandomType = structureRandomType;
        this.seed = seed;
    }

    @Override
    public List<StructureTemplate.StructureBlockInfo> finalizeProcessing(ServerLevelAccessor serverLevel,
            BlockPos offset, BlockPos pos, List<StructureTemplate.StructureBlockInfo> originalBlockInfos,
            List<StructureTemplate.StructureBlockInfo> processedBlockInfos, StructurePlaceSettings settings) {
        List<StructureTemplate.StructureBlockInfo> newBlockInfos = new ArrayList<>(processedBlockInfos.size());
        for (StructureTemplate.StructureBlockInfo blockInfo : processedBlockInfos) {
            StructureTemplate.StructureBlockInfo newBlockInfo = processFinal(serverLevel, offset, pos, blockInfo,
                    blockInfo, settings, processedBlockInfos);
            newBlockInfos.add(newBlockInfo);
        }
        return newBlockInfos;
    }

    public StructureTemplate.StructureBlockInfo processFinal(LevelReader world, BlockPos piecePos,
            BlockPos structurePos, StructureTemplate.StructureBlockInfo rawBlockInfo,
            StructureTemplate.StructureBlockInfo blockInfo, StructurePlaceSettings settings,
            List<StructureTemplate.StructureBlockInfo> processedBlockInfos) {
        RandomSource random = ProcessorUtil.getRandom(structureRandomType, blockInfo.pos(), piecePos, structurePos,
                world, seed);
        BlockPos blockpos = blockInfo.pos();
        if (blockInfo.state().isAir() && random.nextFloat() <= rarity) {
            boolean validSides = validSides(blockpos, processedBlockInfos);
            boolean validUp = !requiresUp || hasDirection(processedBlockInfos, blockpos, Direction.UP);
            boolean validDown = !requiresDown || hasDirection(processedBlockInfos, blockpos, Direction.DOWN);
            if (validSides && validUp && validDown) {
                return new StructureTemplate.StructureBlockInfo(blockpos, blockState, blockInfo.nbt());
            }
        }
        return blockInfo;
    }

    private boolean validSides(BlockPos blockpos, List<StructureTemplate.StructureBlockInfo> processedBlockInfos) {
        if (requiresSides == 0) return true;
        int sides = 0;
        for (Direction direction : Plane.HORIZONTAL) {
            if (hasDirection(processedBlockInfos, blockpos, direction)) {
                sides++;
                if (sides >= requiresSides) return true;
            }
        }
        return false;
    }

    private boolean hasDirection(List<StructureTemplate.StructureBlockInfo> pieceBlocks, BlockPos pos,
            Direction direction) {
        StructureTemplate.StructureBlockInfo block = getBlockInfo(pieceBlocks, pos.mutable().move(direction));
        return isFaceFull(block, direction.getOpposite());
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
}