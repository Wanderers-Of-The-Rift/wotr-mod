package com.wanderersoftherift.wotr.world.level.levelgen.processor;

import com.wanderersoftherift.wotr.init.ModProcessors;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.util.ProcessorUtil;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.util.StructureRandomType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import net.minecraft.util.RandomSource;

import static com.wanderersoftherift.wotr.world.level.levelgen.processor.util.ProcessorUtil.*;
import static com.wanderersoftherift.wotr.world.level.levelgen.processor.util.StructureRandomType.RANDOM_TYPE_CODEC;
import static net.minecraft.core.Direction.*;
import static net.minecraft.world.level.block.Blocks.VINE;
import static net.minecraft.world.level.block.VineBlock.PROPERTY_BY_DIRECTION;

public class VineProcessor extends StructureProcessor {
    public static final MapCodec<VineProcessor> CODEC = RecordCodecBuilder.mapCodec(builder ->
            builder.group(
                    Codec.BOOL.optionalFieldOf("attach_to_wall", true).forGetter(VineProcessor::isAttachToWall),
                    Codec.BOOL.optionalFieldOf("attach_to_ceiling", true).forGetter(VineProcessor::isAttachToCeiling),
                    Codec.FLOAT.fieldOf("rarity").forGetter(VineProcessor::getRarity),
                    RANDOM_TYPE_CODEC.optionalFieldOf("random_type", StructureRandomType.BLOCK).forGetter(VineProcessor::getStructureRandomType)
            ).apply(builder, VineProcessor::new));

    private final boolean attachToWall;
    private final boolean attachToCeiling;
    private final float rarity;
    private final StructureRandomType structureRandomType;

    public VineProcessor(boolean attachToWall, boolean attachToCeiling, float rarity, StructureRandomType structureRandomType) {
        this.attachToWall = attachToWall;
        this.attachToCeiling = attachToCeiling;
        this.rarity = rarity;
        this.structureRandomType = structureRandomType;
    }

    @Override
    public List<StructureTemplate.StructureBlockInfo> finalizeProcessing(ServerLevelAccessor serverLevel, BlockPos offset, BlockPos pos, List<StructureTemplate.StructureBlockInfo> originalBlockInfos, List<StructureTemplate.StructureBlockInfo> processedBlockInfos, StructurePlaceSettings settings) {
        List<StructureTemplate.StructureBlockInfo> newBlockInfos = new ArrayList<>(processedBlockInfos.size());
        for (StructureTemplate.StructureBlockInfo blockInfo : processedBlockInfos) {
            StructureTemplate.StructureBlockInfo newBlockInfo = processFinal(serverLevel, offset, pos, blockInfo, blockInfo, settings, processedBlockInfos);
            newBlockInfos.add(newBlockInfo);
        }
        return newBlockInfos;
    }

    public StructureTemplate.StructureBlockInfo processFinal(LevelReader world, BlockPos piecePos, BlockPos structurePos, StructureTemplate.StructureBlockInfo rawBlockInfo, StructureTemplate.StructureBlockInfo blockInfo, StructurePlaceSettings settings, List<StructureTemplate.StructureBlockInfo> mapByPos) {
        RandomSource random = ProcessorUtil.getRandom(structureRandomType, blockInfo.pos(), piecePos, structurePos, world, Optional.empty());
        BlockState blockstate = blockInfo.state();
        BlockPos blockpos = blockInfo.pos();
        Direction selectedDirection = null;
        if (blockstate.isAir() && random.nextFloat() <= rarity) {
            selectedDirection = selectDirection(mapByPos, blockpos);
        }
        if (selectedDirection == null) {
            return blockInfo;
        } else {
            if(selectedDirection != UP) {
                selectedDirection = settings.getRotation().rotate(selectedDirection);
                if(settings.getRotation() == Rotation.CLOCKWISE_90 || settings.getRotation() == Rotation.COUNTERCLOCKWISE_90) {
                    selectedDirection = selectedDirection.getOpposite();
                }
            }
            BooleanProperty property = PROPERTY_BY_DIRECTION.get(selectedDirection);
            return new StructureTemplate.StructureBlockInfo(blockpos, VINE.defaultBlockState().setValue(property, true), null);
        }
    }

    private Direction selectDirection(List<StructureTemplate.StructureBlockInfo> mapByPos, BlockPos blockpos) {
        if (attachToWall) {
            if(isDirectionPossible(mapByPos, blockpos, NORTH)) {
                return NORTH;
            }
            if(isDirectionPossible(mapByPos, blockpos, EAST)) {
                return EAST;
            }
            if(isDirectionPossible(mapByPos, blockpos, SOUTH)) {
                return SOUTH;
            }
            if(isDirectionPossible(mapByPos, blockpos, WEST)) {
                return WEST;
            }
        }
        if (attachToCeiling) {
            if(isDirectionPossible(mapByPos, blockpos, UP)) {
                return UP;
            }
        }
        return null;
    }

    private boolean isDirectionPossible(List<StructureTemplate.StructureBlockInfo> pieceBlocks, BlockPos pos, Direction direction) {
        StructureTemplate.StructureBlockInfo block = getBlockInfo(pieceBlocks, pos.mutable().move(direction));
        return isFaceFull(block, direction.getOpposite());
    }

    protected StructureProcessorType<?> getType() {
        return ModProcessors.VINES.get();
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
}