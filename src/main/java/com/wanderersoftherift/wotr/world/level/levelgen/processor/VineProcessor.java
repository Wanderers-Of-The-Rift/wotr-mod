package com.wanderersoftherift.wotr.world.level.levelgen.processor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.init.worldgen.WotrProcessors;
import com.wanderersoftherift.wotr.util.FastRandomSource;
import com.wanderersoftherift.wotr.world.level.levelgen.RiftProcessedRoom;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.util.ProcessorUtil;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.util.StructureRandomType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.wanderersoftherift.wotr.world.level.levelgen.processor.util.ProcessorUtil.createRandom;
import static com.wanderersoftherift.wotr.world.level.levelgen.processor.util.ProcessorUtil.getBlockInfo;
import static com.wanderersoftherift.wotr.world.level.levelgen.processor.util.ProcessorUtil.getRandomSeed;
import static com.wanderersoftherift.wotr.world.level.levelgen.processor.util.ProcessorUtil.isFaceFull;
import static com.wanderersoftherift.wotr.world.level.levelgen.processor.util.ProcessorUtil.isFaceFullFast;
import static com.wanderersoftherift.wotr.world.level.levelgen.processor.util.ProcessorUtil.shapeForFaceFullCheck;
import static com.wanderersoftherift.wotr.world.level.levelgen.processor.util.StructureRandomType.BLOCK;
import static com.wanderersoftherift.wotr.world.level.levelgen.processor.util.StructureRandomType.RANDOM_TYPE_CODEC;
import static net.minecraft.core.Direction.DOWN;
import static net.minecraft.core.Direction.EAST;
import static net.minecraft.core.Direction.NORTH;
import static net.minecraft.core.Direction.SOUTH;
import static net.minecraft.core.Direction.UP;
import static net.minecraft.core.Direction.WEST;
import static net.minecraft.core.Direction.values;
import static net.minecraft.world.level.block.Blocks.VINE;
import static net.minecraft.world.level.block.VineBlock.PROPERTY_BY_DIRECTION;

public class VineProcessor extends StructureProcessor implements RiftAdjacencyProcessor<VineProcessor.ReplacementData> {
    public static final MapCodec<VineProcessor> CODEC = RecordCodecBuilder
            .mapCodec(builder -> builder
                    .group(Codec.BOOL.optionalFieldOf("attach_to_wall", true).forGetter(VineProcessor::isAttachToWall),
                            Codec.BOOL.optionalFieldOf("attach_to_ceiling", true)
                                    .forGetter(VineProcessor::isAttachToCeiling),
                            Codec.FLOAT.fieldOf("rarity").forGetter(VineProcessor::getRarity),
                            RANDOM_TYPE_CODEC.optionalFieldOf("random_type", StructureRandomType.BLOCK)
                                    .forGetter(VineProcessor::getStructureRandomType))
                    .apply(builder, VineProcessor::new));
    private static final List<Direction> HORIZONTAL = Direction.Plane.HORIZONTAL.stream().toList();
    private static final List<Direction> DIRECTIONS = Arrays.stream(values()).toList();

    private final boolean attachToWall;
    private final boolean attachToCeiling;
    private final float rarity;
    private final StructureRandomType structureRandomType;
    private final PositionalRandomFactory rngFactory;

    public VineProcessor(boolean attachToWall, boolean attachToCeiling, float rarity,
            StructureRandomType structureRandomType) {
        this.attachToWall = attachToWall;
        this.attachToCeiling = attachToCeiling;
        this.rarity = rarity;
        this.structureRandomType = structureRandomType;
        rngFactory = FastRandomSource.positional(96451348449641312L);
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
            List<StructureTemplate.StructureBlockInfo> mapByPos) {
        RandomSource random = ProcessorUtil.getRandom(structureRandomType, blockInfo.pos(), piecePos, structurePos,
                world, Optional.empty());
        BlockState blockstate = blockInfo.state();
        BlockPos blockpos = blockInfo.pos();
        Direction selectedDirection = null;
        if (blockstate.isAir() && random.nextFloat() <= rarity) {
            selectedDirection = selectDirection(mapByPos, blockpos);
        }
        if (selectedDirection == null) {
            return blockInfo;
        } else {
            if (selectedDirection != UP) {
                selectedDirection = settings.getRotation().rotate(selectedDirection);
                if (settings.getRotation() == Rotation.CLOCKWISE_90
                        || settings.getRotation() == Rotation.COUNTERCLOCKWISE_90) {
                    selectedDirection = selectedDirection.getOpposite();
                }
            }
            BooleanProperty property = PROPERTY_BY_DIRECTION.get(selectedDirection);
            return new StructureTemplate.StructureBlockInfo(blockpos, VINE.defaultBlockState().setValue(property, true),
                    null);
        }
    }

    private Direction selectDirection(List<StructureTemplate.StructureBlockInfo> mapByPos, BlockPos blockpos) {
        if (attachToWall) {
            if (isDirectionPossible(mapByPos, blockpos, NORTH)) {
                return NORTH;
            }
            if (isDirectionPossible(mapByPos, blockpos, EAST)) {
                return EAST;
            }
            if (isDirectionPossible(mapByPos, blockpos, SOUTH)) {
                return SOUTH;
            }
            if (isDirectionPossible(mapByPos, blockpos, WEST)) {
                return WEST;
            }
        }
        if (attachToCeiling) {
            if (isDirectionPossible(mapByPos, blockpos, UP)) {
                return UP;
            }
        }
        return null;
    }

    private boolean isDirectionPossible(
            List<StructureTemplate.StructureBlockInfo> pieceBlocks,
            BlockPos pos,
            Direction direction) {
        StructureTemplate.StructureBlockInfo block = getBlockInfo(pieceBlocks, pos.mutable().move(direction));
        return isFaceFull(block, direction.getOpposite());
    }

    private Direction selectDirection(RiftProcessedRoom room, BlockPos blockpos) {
        if (attachToWall) {
            for (int i = 0; i < HORIZONTAL.size(); i++) {
                var horizontal = HORIZONTAL.get(i);
                if (isDirectionPossible(room, blockpos, horizontal)) {
                    return horizontal;
                }
            }
        }
        if (attachToCeiling) {
            if (isDirectionPossible(room, blockpos, UP)) {
                return UP;
            }
        }
        return null;
    }

    private boolean isDirectionPossible(RiftProcessedRoom room, BlockPos pos, Direction direction) {
        var state = room.getBlock(pos.getX() + direction.getStepX(), pos.getY() + direction.getStepY(),
                pos.getZ() + direction.getStepZ());
        return state != null && isFaceFullFast(state, pos, direction.getOpposite());
    }

    private Direction selectDirection(BlockState... directions) {
        if (attachToWall) {
            for (int i = 0; i < HORIZONTAL.size(); i++) {
                var horizontal = HORIZONTAL.get(i);
                if (isDirectionPossible(directions[horizontal.ordinal()], horizontal)) {
                    return horizontal;
                }
            }
        }
        if (attachToCeiling) {
            if (isDirectionPossible(directions[UP.ordinal()], UP)) {
                return UP;
            }
        }
        return null;
    }

    private boolean isDirectionPossible(BlockState state, Direction direction) {
        return state != null && isFaceFullFast(state, BlockPos.ZERO, direction.getOpposite());
    }

    protected StructureProcessorType<?> getType() {
        return WotrProcessors.VINES.get();
    }

    public boolean isAttachToWall() {
        return attachToWall;
    }

    public boolean isAttachToCeiling() {
        return attachToCeiling;
    }

    public float getRarity() {
        return rarity;
    }

    public StructureRandomType getStructureRandomType() {
        return structureRandomType;
    }

    // @Override
    public void finalizeRoomProcessing(
            RiftProcessedRoom room,
            ServerLevelAccessor world,
            BlockPos structurePos,
            Vec3i pieceSize) {
        var blockRandomFlag = structureRandomType == BLOCK;
        RandomSource random = createRandom(getRandomSeed(structurePos, 0L));
        // ProcessorUtil.getRandom(/*because block is cached, it's (apparently) not thread safe so it can't be
        // used*/blockRandomFlag ? PIECE : structureRandomType, null, structurePos, new BlockPos(0,0,0), world,
        // Optional.empty());
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
                            bp.set(x2, y2, z2);
                            var direction = selectDirection(room, bp);
                            if (direction == null) {
                                continue;
                            }
                            BooleanProperty property = PROPERTY_BY_DIRECTION.get(direction);
                            room.setBlock(x2, y2, z2, VINE.defaultBlockState().setValue(property, true));
                        }
                    }
                }
            }
        }
    }

    @Override
    public int processAdjacency(ReplacementData data, BlockState[] directionBlocks, boolean isHidden) {
        var old = directionBlocks[6];
        var result = 0;
        if (!isHidden && !old.isAir()) {
            VoxelShape shape = null;
            if (attachToCeiling) {
                var otherBlock = directionBlocks[0];
                if (otherBlock == null || otherBlock.isAir()) {
                    if (data.recalculateChance() < rarity) {
                        shape = shapeForFaceFullCheck(old, BlockPos.ZERO);
                        if (isFaceFullFast(shape, DOWN)) {
                            BooleanProperty property = PROPERTY_BY_DIRECTION.get(DOWN.getOpposite());
                            directionBlocks[0] = VINE.defaultBlockState().setValue(property, true);
                            result |= 1;
                        }
                    }
                } else if (otherBlock.getBlock() == VINE) {
                    if (data.recalculateChance() < rarity) {
                        shape = shapeForFaceFullCheck(old, BlockPos.ZERO);
                        if (isFaceFullFast(shape, DOWN)) {
                            BooleanProperty property = PROPERTY_BY_DIRECTION.get(DOWN.getOpposite());
                            directionBlocks[0] = otherBlock.setValue(property, true);
                            result |= 1;
                        }
                    }
                }
            }
            if (attachToWall) {
                for (int i = 2; i < 6; i++) {
                    var otherBlock = directionBlocks[i];
                    var direction = DIRECTIONS.get(i);
                    if (otherBlock == null || otherBlock.isAir()) {
                        if (data.recalculateChance() < rarity) {
                            if (shape == null) {
                                shape = shapeForFaceFullCheck(old, BlockPos.ZERO);
                            }
                            if (isFaceFullFast(shape, direction)) {
                                BooleanProperty property = PROPERTY_BY_DIRECTION.get(direction.getOpposite());
                                directionBlocks[i] = VINE.defaultBlockState().setValue(property, true);
                                result |= 1 << i;
                            }
                        }
                    } else if (otherBlock.getBlock() == VINE) {
                        if (data.recalculateChance() < rarity) {
                            if (shape == null) {
                                shape = shapeForFaceFullCheck(old, BlockPos.ZERO);
                            }
                            if (isFaceFullFast(shape, direction)) {
                                BooleanProperty property = PROPERTY_BY_DIRECTION.get(direction.getOpposite());
                                directionBlocks[i] = otherBlock.setValue(property, true);
                                result |= 1 << i;
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    @Override
    public VineProcessor.ReplacementData createData(BlockPos structurePos, Vec3i pieceSize, ServerLevelAccessor world) {
        return new VineProcessor.ReplacementData(
                rngFactory.at(structurePos.getX(), structurePos.getY(), structurePos.getZ()),
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