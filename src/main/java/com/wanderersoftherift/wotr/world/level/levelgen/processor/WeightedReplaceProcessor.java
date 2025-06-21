package com.wanderersoftherift.wotr.world.level.levelgen.processor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.util.FastWeightedList;
import com.wanderersoftherift.wotr.util.Ref;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.input.InputBlockState;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.output.OutputBlockState;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.util.ProcessorUtil;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.util.StructureRandomType;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.util.WeightedBlockstateEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import oshi.util.tuples.Pair;

import javax.annotation.Nullable;
import java.util.List;

import static com.wanderersoftherift.wotr.init.worldgen.WotrProcessors.WEIGHTED_REPLACE;
import static com.wanderersoftherift.wotr.world.level.levelgen.processor.util.StructureRandomType.RANDOM_TYPE_CODEC;

public class WeightedReplaceProcessor extends StructureProcessor implements RiftTemplateProcessor {
    public static final MapCodec<WeightedReplaceProcessor> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            WeightedBlockstateEntry.CODEC.listOf()
                    .fieldOf("output_list")
                    .forGetter(WeightedReplaceProcessor::getWeightList),
            InputBlockState.DIRECT_CODEC.fieldOf("input_state").forGetter(WeightedReplaceProcessor::getInputBlockState),
            RANDOM_TYPE_CODEC.optionalFieldOf("random_type", StructureRandomType.BLOCK)
                    .forGetter(WeightedReplaceProcessor::getStructureRandomType),
            Codec.LONG.optionalFieldOf("seed_adjustment", 6551687435L)
                    .forGetter(WeightedReplaceProcessor::getSeedAdjustment)
    ).apply(builder, WeightedReplaceProcessor::new));

    private final List<WeightedBlockstateEntry> weightList;
    private final FastWeightedList<OutputBlockState> fastWeightList;
    private final InputBlockState inputBlockState;
    private final StructureRandomType structureRandomType;
    private final long seedAdjustment;

    public WeightedReplaceProcessor(List<WeightedBlockstateEntry> weightList, InputBlockState inputBlockState,
            StructureRandomType structureRandomType, long seedAdjustment) {
        this.weightList = weightList;
        this.inputBlockState = inputBlockState;
        this.structureRandomType = structureRandomType;
        this.seedAdjustment = seedAdjustment;
        var weightedData = weightList.stream()
                .map(it -> new Pair<>((float) it.getWeight().asInt(), it.outputBlockState()))
                .toArray(Pair[]::new);
        fastWeightList = FastWeightedList.of(weightedData);
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
        return backportProcess(world, piecePos, structurePos, rawBlockInfo, blockInfo, settings, template);
    }

    private BlockState getReplacementBlock(LevelReader world, int x, int y, int z, BlockPos structurePos) {
        if (!fastWeightList.isEmpty()) {
            var rng = ProcessorUtil.getRandom(structureRandomType, new BlockPos(x, y, z), structurePos, BlockPos.ZERO,
                    world, seedAdjustment);
            return fastWeightList.random(rng).convertBlockState();
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

    @Override
    public BlockState processBlockState(
            BlockState currentState,
            int x,
            int y,
            int z,
            ServerLevelAccessor world,
            BlockPos structurePos,
            Ref<BlockEntity> entityRef,
            boolean isVisible) {
        if (!inputBlockState.matchesBlockstate(currentState)) {
            return currentState;
        }

        BlockState newBlockState = getReplacementBlock(world, x, y, z, structurePos);
        if (newBlockState == null) {
            return currentState;
        }

        return ProcessorUtil.copyState(currentState, newBlockState);
    }
}