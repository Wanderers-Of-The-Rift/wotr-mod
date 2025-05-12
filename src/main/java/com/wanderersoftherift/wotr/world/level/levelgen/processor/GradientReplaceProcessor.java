package com.wanderersoftherift.wotr.world.level.levelgen.processor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.util.FibonacciHashing;
import com.wanderersoftherift.wotr.util.OpenSimplex2F;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.input.InputBlockState;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.output.OutputBlockState;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.util.ProcessorUtil;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.NotNull;
import oshi.util.tuples.Pair;

import javax.annotation.Nullable;
import java.lang.ref.PhantomReference;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.wanderersoftherift.wotr.init.ModProcessors.GRADIENT_SPOT_REPLACE;

public class GradientReplaceProcessor extends StructureProcessor implements RiftTemplateProcessor {
    public static final MapCodec<GradientReplaceProcessor> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            InputToOutputs.CODEC.listOf()
                    .fieldOf("replacements")
                    .xmap(InputToOutputs::toMap, InputToOutputs::toInputToOutputs)
                    .forGetter(GradientReplaceProcessor::getReplaceMap),
            Codec.DOUBLE.optionalFieldOf("noise_scale_x", 0.075D).forGetter(GradientReplaceProcessor::getNoiseScaleX),
            Codec.DOUBLE.optionalFieldOf("noise_scale_y", 0.075D).forGetter(GradientReplaceProcessor::getNoiseScaleY),
            Codec.DOUBLE.optionalFieldOf("noise_scale_z", 0.075D).forGetter(GradientReplaceProcessor::getNoiseScaleZ),
            Codec.INT.optionalFieldOf("seed_adjustment", 0).forGetter(GradientReplaceProcessor::getSeedAdjustment)
    ).apply(builder, GradientReplaceProcessor::new));

    protected static Map<Long, OpenSimplex2F> noiseGenSeeds = new ConcurrentHashMap<>();

    private final Map<InputBlockState, List<OutputStep>> replaceMap;
    private final Map<Block,List<Pair<InputBlockState, OutputSteps>>> betterReplaceMap;
    private final double noiseScaleX;
    private final double noiseScaleY;
    private final double noiseScaleZ;
    private final int seedAdjustment;

    private Pair<PhantomReference<LevelReader>,OpenSimplex2F> lastNoiseCache = null;
    private final List<Pair<InputBlockState, OutputSteps>>[] fastBetterReplaceMapValues;
    private final Block[] fastBetterReplaceMapKeys;

    public GradientReplaceProcessor(Map<InputBlockState, List<OutputStep>> replaceMap, double noiseScaleX,
            double noiseScaleY, double noiseScaleZ, int seedAdjustment) {
        this.replaceMap = new Object2ObjectLinkedOpenHashMap<>(replaceMap);
        var betterReplaceMap=new IdentityHashMap<Block,List<Pair<InputBlockState, OutputSteps>>>();
        replaceMap.forEach((inputState,outputStates)->{
            var list = betterReplaceMap.computeIfAbsent(inputState.block(),(block)->new ArrayList<>());
            list.add(new Pair<>(inputState, new OutputSteps(outputStates)));
        });
        this.betterReplaceMap=betterReplaceMap;
        this.noiseScaleX = noiseScaleX;
        this.noiseScaleY = noiseScaleY;
        this.noiseScaleZ = noiseScaleZ;
        this.seedAdjustment = seedAdjustment;
        fastBetterReplaceMapValues = new List[128];
        fastBetterReplaceMapKeys = new Block[128];
        var iter = betterReplaceMap.entrySet().iterator();
        while (iter.hasNext()){
            var entry = iter.next();
            var hash = hashBlock(entry.getKey());
            if(fastBetterReplaceMapKeys[hash]==null){
                fastBetterReplaceMapKeys[hash]=entry.getKey();
                fastBetterReplaceMapValues[hash]=entry.getValue();
                iter.remove();
            }
        }

    }

    private int hashBlock(Block b){
        return (System.identityHashCode(b)* FibonacciHashing.GOLDEN_RATIO_INT)>>>25;
    }

    public OpenSimplex2F getNoiseGen(@NotNull Long seed) {
        var noiseGen = noiseGenSeeds.get(seed);
        if(noiseGen!=null){
            return noiseGen;
        }
        return noiseGenSeeds.computeIfAbsent(seed, OpenSimplex2F::new);
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

        var newBlockState = processBlockState(blockInfo.state(), blockInfo.pos().getX(), blockInfo.pos().getX(), blockInfo.pos().getX(), (ServerLevel) world, piecePos, blockInfo.nbt(), true);

        return new StructureTemplate.StructureBlockInfo(blockInfo.pos(), newBlockState, blockInfo.nbt());
    }

    @Override
    public BlockState processBlockState(BlockState blockstate, int x, int y, int z, ServerLevelAccessor world, BlockPos structurePos, CompoundTag nbt, boolean isVisible) {
        Block block = blockstate.getBlock();
        var blockHash = hashBlock(block);
        List<Pair<InputBlockState, OutputSteps>> multiOutputState;
        if(fastBetterReplaceMapKeys[blockHash]==block){
            multiOutputState = fastBetterReplaceMapValues[blockHash];
        }else {
            multiOutputState = betterReplaceMap.get(block);
            if(multiOutputState==null){
                return blockstate;
            }
        }
        for (var entry : multiOutputState) {

            if (entry.getA().matchesBlockstateAssumingBlockEqual(blockstate)) {
                var steps = entry.getB();
                if(steps.isEmpty()){
                    return blockstate;
                }
                return getOutputBlockState(steps, world, structurePos, x, y, z, blockstate, isVisible);
            }
        }
        return blockstate;
    }

    private BlockState getOutputBlockState(
            OutputSteps outputSteps,
            LevelReader world,
            BlockPos structurePos,
            int x, int y, int z,
            BlockState blockstate,
            boolean isVisible) {
        OpenSimplex2F noiseGen = getNoiseGen(world, structurePos);
        BlockState newBlockState = getReplacementBlock(outputSteps, x, y, z, noiseGen, isVisible);
        if (newBlockState == null) {
            return blockstate;
        }

        return ProcessorUtil.copyState(blockstate, newBlockState);
    }

    private OpenSimplex2F getNoiseGen(LevelReader world, BlockPos structurePos) {
        world = world instanceof ServerLevelAccessor sa ? sa.getLevel() : world;
        var currentCache = lastNoiseCache;
        if (world!=null && currentCache!=null && currentCache.getA().refersTo(world)) {
            return currentCache.getB();
        }
        OpenSimplex2F noiseGen = null;
        if (world instanceof ServerLevelAccessor serverLevelAccessor) {
            noiseGen = getNoiseGen(serverLevelAccessor.getLevel().getSeed() + seedAdjustment);
            lastNoiseCache = new Pair(new PhantomReference<>(world, null), noiseGen);
        } else {
            noiseGen = getNoiseGen(structurePos.asLong() + seedAdjustment);
        }
        return noiseGen;
    }

    private BlockState getReplacementBlock(OutputSteps outputSteps, int x, int y, int z, OpenSimplex2F noiseGen, boolean isVisible) {
        if (outputSteps.isEmpty()){
            return null;
        }
        if(outputSteps.size() == 1 || !isVisible){
            return outputSteps.convertedBlockStates[0];
        }
        double noiseValue = Math.abs(noiseGen.noise3_Classic(x * getNoiseScaleX(),
                y * getNoiseScaleY(), z * getNoiseScaleZ()));
        float stepSize = 0;
        var sizes = outputSteps.stepSizes;
        for (int i = 0; i < outputSteps.size(); i++) {
            stepSize += sizes[i];
            if (noiseValue < stepSize) {
                return outputSteps.convertedBlockStates[i];
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

    public double getNoiseScaleX() {
        return noiseScaleX;
    }

    public double getNoiseScaleY() {
        return noiseScaleY;
    }

    public double getNoiseScaleZ() {
        return noiseScaleZ;
    }

    private record InputToOutputs(InputBlockState inputBlockState, List<OutputStep> outputSteps) {
        public static final Codec<InputToOutputs> CODEC = RecordCodecBuilder.create(builder -> builder
                .group(InputBlockState.DIRECT_CODEC.fieldOf("input_state").forGetter(InputToOutputs::inputBlockState),
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
        public static final Codec<OutputStep> CODEC = RecordCodecBuilder.create(builder -> builder
                .group(OutputBlockState.DIRECT_CODEC.fieldOf("output_state").forGetter(OutputStep::outputBlockState),
                        Codec.floatRange(0, 1).fieldOf("step_size").forGetter(OutputStep::stepSize)
                ).apply(builder, OutputStep::new));
    }

    private static class OutputSteps{
        private final float[] stepSizes;
        private final BlockState[] convertedBlockStates;
        private final int size;

        private OutputSteps(List<OutputStep> steps) {
            this.size=steps.size();
            stepSizes = new float[steps.size()];
            convertedBlockStates = new BlockState[steps.size()];
            for (int i = 0; i < steps.size(); i++) {
                var step = steps.get(i);
                stepSizes[i]=step.stepSize;
                convertedBlockStates[i] = step.outputBlockState().convertBlockState();
            }
        }

        public boolean isEmpty() {
            return size==0;
        }
        public int size(){
            return size;
        }
    }
}