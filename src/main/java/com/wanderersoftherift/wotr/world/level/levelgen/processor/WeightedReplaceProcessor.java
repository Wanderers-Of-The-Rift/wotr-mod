package com.wanderersoftherift.wotr.world.level.levelgen.processor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.input.InputBlockState;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.util.ProcessorUtil;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.util.StructureRandomType;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.util.WeightedBlockstateEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.util.random.Weight;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import javax.annotation.Nullable;
import java.util.List;

import static com.wanderersoftherift.wotr.init.ModProcessors.WEIGHTED_REPLACE;
import static com.wanderersoftherift.wotr.world.level.levelgen.processor.util.StructureRandomType.RANDOM_TYPE_CODEC;

public class WeightedReplaceProcessor extends StructureProcessor {
    public static final MapCodec<WeightedReplaceProcessor> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            WeightedBlockstateEntry.CODEC.listOf()
                    .fieldOf("output_list")
                    .forGetter(WeightedReplaceProcessor::getWeightList),
            InputBlockState.DIRECT_CODEC.fieldOf("input_state").forGetter(WeightedReplaceProcessor::getInputBlockState),
            RANDOM_TYPE_CODEC.optionalFieldOf("random_type", StructureRandomType.BLOCK)
                    .forGetter(WeightedReplaceProcessor::getStructureRandomType),
            Codec.LONG.optionalFieldOf("seed_adjustment", 6551687435L)
                    .forGetter(WeightedReplaceProcessor::getSeedAdjustment))
            .apply(builder, WeightedReplaceProcessor::new));

    private final List<WeightedBlockstateEntry> weightList;
    private final InputBlockState inputBlockState;
    private final StructureRandomType structureRandomType;
    private final long seedAdjustment;

    public WeightedReplaceProcessor(List<WeightedBlockstateEntry> weightList, InputBlockState inputBlockState,
            StructureRandomType structureRandomType, long seedAdjustment) {
        this.weightList = weightList;
        this.inputBlockState = inputBlockState;
        this.structureRandomType = structureRandomType;
        this.seedAdjustment = seedAdjustment;
    }

    @Override
    public StructureTemplate.StructureBlockInfo process(LevelReader world, BlockPos piecePos, BlockPos structurePos,
            StructureTemplate.StructureBlockInfo rawBlockInfo, StructureTemplate.StructureBlockInfo blockInfo,
            StructurePlaceSettings settings, @Nullable StructureTemplate template) {
        BlockState blockstate = blockInfo.state();
        BlockPos blockPos = blockInfo.pos();
        ProcessorUtil.getRandom(structureRandomType, blockPos, piecePos, structurePos, world, seedAdjustment);
        if (inputBlockState.matchesBlockstate(blockstate)) {
            return blockInfo;
        }

        BlockState newBlockState = getReplacementBlock(world);
        if (newBlockState == null) {
            return blockInfo;
        }

        newBlockState = ProcessorUtil.copyState(blockstate, newBlockState);
        return new StructureTemplate.StructureBlockInfo(blockPos, newBlockState, blockInfo.nbt());
    }

    private BlockState getReplacementBlock(LevelReader world) {
        if (world instanceof WorldGenLevel worldGenLevel) {
            return WeightedRandom.getRandomItem(worldGenLevel.getRandom(), weightList)
                    .orElse(new WeightedBlockstateEntry(null, Weight.of(1)))
                    .outputBlockState()
                    .convertBlockState();
        }
        return null;
    }

    protected StructureProcessorType<?> getType() {
        return WEIGHTED_REPLACE.get();
    }

    public List<WeightedBlockstateEntry> getWeightList() {
        return weightList;
    }

    public StructureRandomType getStructureRandomType() {
        return structureRandomType;
    }

    public long getSeedAdjustment() {
        return seedAdjustment;
    }

    public InputBlockState getInputBlockState() {
        return inputBlockState;
    }
}