package com.wanderersoftherift.wotr.world.level.levelgen.processor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.codec.OutputStateCodecs;
import com.wanderersoftherift.wotr.world.level.levelgen.RiftProcessedRoom;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.util.ProcessorUtil;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.util.StructureRandomType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.wanderersoftherift.wotr.init.worldgen.WotrProcessors.ATTACHMENT;
import static com.wanderersoftherift.wotr.world.level.levelgen.processor.util.ProcessorUtil.createRandom;
import static com.wanderersoftherift.wotr.world.level.levelgen.processor.util.ProcessorUtil.getBlockInfo;
import static com.wanderersoftherift.wotr.world.level.levelgen.processor.util.ProcessorUtil.getRandomSeed;
import static com.wanderersoftherift.wotr.world.level.levelgen.processor.util.ProcessorUtil.isFaceFull;
import static com.wanderersoftherift.wotr.world.level.levelgen.processor.util.ProcessorUtil.isFaceFullFast;
import static com.wanderersoftherift.wotr.world.level.levelgen.processor.util.StructureRandomType.BLOCK;
import static com.wanderersoftherift.wotr.world.level.levelgen.processor.util.StructureRandomType.RANDOM_TYPE_CODEC;
import static net.minecraft.core.Direction.Plane;

public class AttachmentProcessor extends StructureProcessor
        implements ReplaceAirBySurroundingRiftProcessor<AttachmentProcessor.ReplacementData> {
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
    public List<StructureTemplate.StructureBlockInfo> finalizeProcessing(
            ServerLevelAccessor serverLevel,
            BlockPos offset,
            BlockPos pos,
            List<StructureTemplate.StructureBlockInfo> originalBlockInfos,
            List<StructureTemplate.StructureBlockInfo> processedBlockInfos,
            StructurePlaceSettings settings) {
        List<StructureTemplate.StructureBlockInfo> newBlockInfos = new ArrayList<>(processedBlockInfos.size());
        for (StructureTemplate.StructureBlockInfo blockInfo : processedBlockInfos) {
            StructureTemplate.StructureBlockInfo newBlockInfo = processFinal(serverLevel, offset, pos, blockInfo,
                    blockInfo, settings, processedBlockInfos);
            newBlockInfos.add(newBlockInfo);
        }
        return newBlockInfos;
    }

    public StructureTemplate.StructureBlockInfo processFinal(
            LevelReader world,
            BlockPos piecePos,
            BlockPos structurePos,
            StructureTemplate.StructureBlockInfo rawBlockInfo,
            StructureTemplate.StructureBlockInfo blockInfo,
            StructurePlaceSettings settings,
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
        if (requiresSides == 0) {
            return true;
        }
        int sides = 0;
        for (Direction direction : Plane.HORIZONTAL) {
            if (hasDirection(processedBlockInfos, blockpos, direction)) {
                sides++;
                if (sides >= requiresSides) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasDirection(
            List<StructureTemplate.StructureBlockInfo> pieceBlocks,
            BlockPos pos,
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

    public void finalizeRoomProcessing(
            RiftProcessedRoom room,
            ServerLevelAccessor world,
            BlockPos structurePos,
            Vec3i pieceSize) {
        var blockRandomFlag = structureRandomType == BLOCK;
        RandomSource random = createRandom(getRandomSeed(structurePos, seed.orElse(0L)));
        // ProcessorUtil.getRandom(blockRandomFlag ? PIECE : structureRandomType, null, structurePos, new
        // BlockPos(0,0,0), world, seed);
        var roll = random.nextFloat();
        var bp = new BlockPos.MutableBlockPos();
        for (int x = 0; x < pieceSize.getX(); x++) {
            for (int z = 0; z < pieceSize.getZ(); z++) {
                for (int y = 0; y < pieceSize.getY(); y++) {
                    var x2 = x + structurePos.getX();
                    var y2 = y + structurePos.getY();
                    var z2 = z + structurePos.getZ();
                    var currentState = room.getBlock(x2, y2, z2);
                    if (currentState != null && currentState.isAir()) {
                        if (blockRandomFlag) {
                            roll = random.nextFloat();
                        }
                        if (roll <= rarity) {
                            int sideCount = requiresSides;
                            BlockState newBlock;
                            for (int i = 0; i < HORIZONTAL.size(); i++) {
                                var side = HORIZONTAL.get(i);
                                if (sideCount <= 0) {
                                    break;
                                }
                                newBlock = room.getBlock(x2 + side.getStepX(), y2, z2 + side.getStepZ());
                                bp.set(x2 + side.getStepX(), y2, z2 + side.getStepZ());
                                if (newBlock != null && !isFaceFullFast(newBlock, bp, side.getOpposite())) {
                                    sideCount--;
                                }
                            }

                            if (sideCount > 0) {
                                continue;
                            }
                            newBlock = room.getBlock(x2, y2 + 1, z2);
                            bp.set(x2, y2 + 1, z2);
                            boolean validUp = !requiresUp || newBlock == null
                                    || isFaceFullFast(newBlock, bp, Direction.DOWN);
                            if (!validUp) {
                                continue;
                            }
                            newBlock = room.getBlock(x2, y2 - 1, z2);
                            bp.setY(y2 - 1);
                            boolean validDown = !requiresDown || newBlock == null
                                    || isFaceFullFast(newBlock, bp, Direction.UP);
                            if (!validDown) {
                                continue;
                            }
                            room.setBlock(x2, y2, z2, blockState);

                        }
                    }
                }
            }
        }
    }

    @Override
    public BlockState replace(
            AttachmentProcessor.ReplacementData data,
            BlockState up,
            BlockState down,
            BlockState north,
            BlockState south,
            BlockState east,
            BlockState west,
            BlockState[] directions) {
        if (data.recalculateChance() <= rarity) {
            int sideCount = requiresSides;
            for (int i = 0; i < HORIZONTAL.size() && sideCount > 0; i++) {
                var side = HORIZONTAL.get(i);
                var directionBlock = directions[side.ordinal()];
                if (directionBlock != null && !isFaceFullFast(directionBlock, BlockPos.ZERO, side.getOpposite())) {
                    sideCount--;
                }
            }

            if (sideCount > 0) {
                return Blocks.AIR.defaultBlockState();
            }

            boolean validUp = !requiresUp || up == null || isFaceFullFast(up, BlockPos.ZERO, Direction.DOWN);
            if (!validUp) {
                return Blocks.AIR.defaultBlockState();
            }

            boolean validDown = !requiresDown || down == null || isFaceFullFast(down, BlockPos.ZERO, Direction.UP);
            if (!validDown) {
                return Blocks.AIR.defaultBlockState();
            }
            return blockState;
        }
        return Blocks.AIR.defaultBlockState();
    }

    @Override
    public AttachmentProcessor.ReplacementData createData(BlockPos structurePos, Vec3i pieceSize) {
        // todo make RNG that doesn't trash performance
        return new AttachmentProcessor.ReplacementData(
                createRandom(getRandomSeed(structurePos, seed.orElse(0L))), structureRandomType == BLOCK
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