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
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Arrays;
import java.util.List;

import static com.wanderersoftherift.wotr.world.level.levelgen.processor.util.ProcessorUtil.isFaceFullFast;
import static com.wanderersoftherift.wotr.world.level.levelgen.processor.util.ProcessorUtil.shapeForFaceFullCheck;
import static com.wanderersoftherift.wotr.world.level.levelgen.processor.util.StructureRandomType.BLOCK;
import static com.wanderersoftherift.wotr.world.level.levelgen.processor.util.StructureRandomType.RANDOM_TYPE_CODEC;
import static net.minecraft.core.Direction.DOWN;
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

    public VineProcessor(boolean attachToWall, boolean attachToCeiling, float rarity,
            StructureRandomType structureRandomType) {
        this.attachToWall = attachToWall;
        this.attachToCeiling = attachToCeiling;
        this.rarity = rarity;
        this.structureRandomType = structureRandomType;
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

    @Override
    public int processAdjacency(ReplacementData data, BlockState[] adjacentBlocks, boolean isHidden) {
        var old = adjacentBlocks[6];
        var result = 0;
        if (!isHidden && !old.isAir()) {
            VoxelShape shape = null;
            if (attachToCeiling) {
                var otherBlock = adjacentBlocks[0];
                if (otherBlock == null || otherBlock.isAir()) {
                    if (data.recalculateChance() < rarity) {
                        shape = shapeForFaceFullCheck(old, BlockPos.ZERO);
                        if (isFaceFullFast(shape, DOWN)) {
                            BooleanProperty property = PROPERTY_BY_DIRECTION.get(DOWN.getOpposite());
                            adjacentBlocks[0] = VINE.defaultBlockState().setValue(property, true);
                            result |= 1;
                        }
                    }
                } else if (otherBlock.getBlock() == VINE) {
                    if (data.recalculateChance() < rarity) {
                        shape = shapeForFaceFullCheck(old, BlockPos.ZERO);
                        if (isFaceFullFast(shape, DOWN)) {
                            BooleanProperty property = PROPERTY_BY_DIRECTION.get(DOWN.getOpposite());
                            adjacentBlocks[0] = otherBlock.setValue(property, true);
                            result |= 1;
                        }
                    }
                }
            }
            if (attachToWall) {
                for (int i = 2; i < 6; i++) {
                    var otherBlock = adjacentBlocks[i];
                    var direction = DIRECTIONS.get(i);
                    if (otherBlock == null || otherBlock.isAir()) {
                        if (data.recalculateChance() < rarity) {
                            if (shape == null) {
                                shape = shapeForFaceFullCheck(old, BlockPos.ZERO);
                            }
                            if (isFaceFullFast(shape, direction)) {
                                BooleanProperty property = PROPERTY_BY_DIRECTION.get(direction.getOpposite());
                                adjacentBlocks[i] = VINE.defaultBlockState().setValue(property, true);
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
                                adjacentBlocks[i] = otherBlock.setValue(property, true);
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
                ProcessorUtil.getRiftRandomFactory(world, 461131846161L)
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