package com.wanderersoftherift.wotr.world.level.levelgen.processor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.util.OpenSimplex2F;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.input.InputBlockState;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.output.OutputBlockState;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.util.ProcessorUtil;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.wanderersoftherift.wotr.init.ModProcessors.GRADIENT_SPOT_REPLACE;

public class GradientReplaceProcessor extends StructureProcessor {
    public static final MapCodec<GradientReplaceProcessor> CODEC = RecordCodecBuilder.mapCodec(builder ->
            builder.group(
                    InputToOutputs.CODEC.listOf().fieldOf("replacements")
                            .xmap(InputToOutputs::toMap, InputToOutputs::toInputToOutputs).forGetter(GradientReplaceProcessor::getReplaceMap),
                    Codec.INT.optionalFieldOf("seed_adjustment", 0).forGetter(GradientReplaceProcessor::getSeedAdjustment)
            ).apply(builder, GradientReplaceProcessor::new));

    private final Map<InputBlockState, List<OutputStep>> replaceMap;
    private final int seedAdjustment;

    protected static Map<Long, OpenSimplex2F> noiseGenSeeds = new HashMap<>();

    public GradientReplaceProcessor(Map<InputBlockState, List<OutputStep>> replaceMap, int seedAdjustment) {
        this.replaceMap = replaceMap;
        this.seedAdjustment = seedAdjustment;
    }

    public OpenSimplex2F getNoiseGen(long seed) {
        return noiseGenSeeds.computeIfAbsent(seed, OpenSimplex2F::new);
    }

    @Override
    public StructureTemplate.StructureBlockInfo process(LevelReader world, BlockPos piecePos, BlockPos structurePos, StructureTemplate.StructureBlockInfo rawBlockInfo, StructureTemplate.StructureBlockInfo blockInfo, StructurePlaceSettings settings, @Nullable StructureTemplate template) {

        BlockState blockstate = blockInfo.state();
        BlockPos blockPos = blockInfo.pos();
        for (Map.Entry<InputBlockState, List<OutputStep>> entry : replaceMap.entrySet()) {
            if (entry.getKey().matchesBlockstate(blockstate)) {
                return getOutputBlockInfo(entry.getValue(), world, structurePos, blockInfo, blockPos, blockstate);
            }
        }
        return blockInfo;
    }

    private StructureTemplate.StructureBlockInfo getOutputBlockInfo(List<OutputStep> outputSteps, LevelReader world, BlockPos structurePos, StructureTemplate.StructureBlockInfo blockInfo, BlockPos blockPos, BlockState blockstate) {
        OpenSimplex2F noiseGen = getNoiseGen(world, structurePos);
        BlockState newBlockState = getReplacementBlock(outputSteps, blockPos, noiseGen);
        if(newBlockState == null){
            return blockInfo;
        }

        newBlockState = ProcessorUtil.copyState(blockstate, newBlockState);
        return new StructureTemplate.StructureBlockInfo(blockPos, newBlockState, blockInfo.nbt());
    }

    private OpenSimplex2F getNoiseGen(LevelReader world, BlockPos structurePos) {
        OpenSimplex2F noiseGen = null;
        if(world instanceof WorldGenLevel) {
            noiseGen = getNoiseGen(((WorldGenLevel) world).getSeed()+seedAdjustment);
        }else{
            noiseGen = getNoiseGen(structurePos.asLong()+seedAdjustment);
        }
        return noiseGen;
    }

    private BlockState getReplacementBlock(List<OutputStep> outputSteps, BlockPos blockPos, OpenSimplex2F noiseGen) {
        double noiseValue = (noiseGen.noise3_Classic(blockPos.getX() * 0.075D, blockPos.getY() * 0.075D, blockPos.getZ() * 0.075D));
        float stepSize = 0;
        for(OutputStep outputStep: outputSteps){
            stepSize = stepSize+outputStep.stepSize;
            if (noiseValue < stepSize && noiseValue > (stepSize * -1)) {
                return outputStep.outputBlockState.convertBlockState();
            }
        }
        return null;
    }

    protected StructureProcessorType<?> getType() {
        return GRADIENT_SPOT_REPLACE.get();
    }

    public Map<InputBlockState, List<OutputStep>> getReplaceMap() {
        return replaceMap;
    }

    public int getSeedAdjustment() {
        return seedAdjustment;
    }

    private record InputToOutputs(InputBlockState inputBlockState, List<OutputStep> outputSteps) {
        public static final Codec<InputToOutputs> CODEC = RecordCodecBuilder.create(builder ->
                builder.group(
                        InputBlockState.DIRECT_CODEC.fieldOf("input_state").forGetter(InputToOutputs::inputBlockState),
                        OutputStep.CODEC.listOf().fieldOf("output_steps").forGetter(InputToOutputs::outputSteps)
                ).apply(builder, InputToOutputs::new));

        public static Map<InputBlockState, List<OutputStep>> toMap(List<InputToOutputs> inputToOutputs) {
            Map<InputBlockState, List<OutputStep>> map = new Object2ObjectOpenHashMap<>(inputToOutputs.size());
            for (InputToOutputs inputToOutput : inputToOutputs) {
                map.put(inputToOutput.inputBlockState(), inputToOutput.outputSteps());
            }
            return map;
        }

        public static List<InputToOutputs> toInputToOutputs(Map<InputBlockState, List<OutputStep>> map) {
            return map.entrySet().stream().map(entry -> new InputToOutputs(entry.getKey(), entry.getValue())).toList();
        }
    }

    private record OutputStep(OutputBlockState outputBlockState, float stepSize) {
        public static final Codec<OutputStep> CODEC = RecordCodecBuilder.create(builder ->
            builder.group(
                    OutputBlockState.DIRECT_CODEC.fieldOf("output_state").forGetter(OutputStep::outputBlockState),
                    Codec.floatRange(0, 1).fieldOf("step_size").forGetter(OutputStep::stepSize)
            ).apply(builder, OutputStep::new));
    }
}