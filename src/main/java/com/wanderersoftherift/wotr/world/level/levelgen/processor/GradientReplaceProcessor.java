package com.wanderersoftherift.wotr.world.level.levelgen.processor;

import com.wanderersoftherift.wotr.util.OpenSimplex2F;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.output.OutputBlockState;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.util.ProcessorUtil;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
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
import static com.wanderersoftherift.wotr.world.level.levelgen.processor.output.OutputBlockState.DIRECT_CODEC;

public class GradientReplaceProcessor extends StructureProcessor {
    public static final MapCodec<GradientReplaceProcessor> CODEC = RecordCodecBuilder.mapCodec(builder ->
            builder.group(
                    Codec.mapPair(DIRECT_CODEC.fieldOf("output_state"),Codec.floatRange(0, 1).fieldOf("step_size")).codec().listOf().fieldOf("gradient_list").forGetter(GradientReplaceProcessor::getGradientList),
                    Codec.INT.optionalFieldOf("seed_adjustment", 0).forGetter(GradientReplaceProcessor::getSeedAdjustment),
                    BuiltInRegistries.BLOCK.byNameCodec().fieldOf("to_replace").forGetter(GradientReplaceProcessor::getToReplace)
            ).apply(builder, GradientReplaceProcessor::new));

    private final List<Pair<OutputBlockState, Float>> gradientList;
    private final int seedAdjustment;
    private final Block toReplace;

    protected static Map<Long, OpenSimplex2F> noiseGenSeeds = new HashMap<>();

    public GradientReplaceProcessor(List<Pair<OutputBlockState, Float>> gradientList, int seedAdjustment, Block toReplace) {
        this.gradientList = gradientList;
        this.seedAdjustment = seedAdjustment;
        this.toReplace = toReplace;
    }

    public OpenSimplex2F getNoiseGen(long seed) {
        return noiseGenSeeds.computeIfAbsent(seed, OpenSimplex2F::new);
    }

    @Override
    public StructureTemplate.StructureBlockInfo process(LevelReader world, BlockPos piecePos, BlockPos structurePos, StructureTemplate.StructureBlockInfo rawBlockInfo, StructureTemplate.StructureBlockInfo blockInfo, StructurePlaceSettings settings, @Nullable StructureTemplate template) {
        OpenSimplex2F noiseGen = null;
        if(world instanceof WorldGenLevel) {
            noiseGen = getNoiseGen(((WorldGenLevel) world).getSeed()+seedAdjustment);
        }else{
            noiseGen = getNoiseGen(structurePos.asLong()+seedAdjustment);
        }

        BlockState blockstate = blockInfo.state();
        BlockPos blockPos = blockInfo.pos();
        if(!blockstate.is(toReplace)){
            return blockInfo;
        }

        BlockState newBlockState = getReplacementBlock(blockPos, noiseGen);
        if(newBlockState == null){
            return blockInfo;
        }

        if (blockstate.is(BlockTags.STAIRS)) {
            return new StructureTemplate.StructureBlockInfo(blockPos, ProcessorUtil.copyStairsState(blockstate, newBlockState), blockInfo.nbt());
        } else if (blockstate.is(BlockTags.SLABS)) {
            return new StructureTemplate.StructureBlockInfo(blockPos, ProcessorUtil.copySlabState(blockstate, newBlockState), blockInfo.nbt());
        } else if (blockstate.is(BlockTags.WALLS)) {
            return new StructureTemplate.StructureBlockInfo(blockPos, ProcessorUtil.copyWallState(blockstate, newBlockState), blockInfo.nbt());
        }else{
            return new StructureTemplate.StructureBlockInfo(blockPos, newBlockState, blockInfo.nbt());
        }
    }

    private BlockState getReplacementBlock(BlockPos blockPos, OpenSimplex2F noiseGen) {
        double noiseValue = (noiseGen.noise3_Classic(blockPos.getX() * 0.075D, blockPos.getY() * 0.075D, blockPos.getZ() * 0.075D));
        float stepSize = 0;
        for(Pair<OutputBlockState, Float> pair: gradientList){
            stepSize = stepSize+pair.getSecond();
            if (noiseValue < stepSize && noiseValue > (stepSize * -1)) {
                return pair.getFirst().convertBlockState();
            }
        }
        return null;
    }

    protected StructureProcessorType<?> getType() {
        return GRADIENT_SPOT_REPLACE.get();
    }

    public List<Pair<OutputBlockState, Float>> getGradientList() {
        return gradientList;
    }

    public int getSeedAdjustment() {
        return seedAdjustment;
    }

    public Block getToReplace() {
        return toReplace;
    }
}