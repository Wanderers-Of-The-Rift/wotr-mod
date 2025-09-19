package com.wanderersoftherift.wotr.world.level.levelgen.processor;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.block.AnomalyBlock;
import com.wanderersoftherift.wotr.block.blockentity.AnomalyBlockEntity;
import com.wanderersoftherift.wotr.block.blockentity.anomaly.AnomalyReward;
import com.wanderersoftherift.wotr.block.blockentity.anomaly.AnomalyTask;
import com.wanderersoftherift.wotr.init.WotrBlocks;
import com.wanderersoftherift.wotr.init.worldgen.WotrProcessors;
import com.wanderersoftherift.wotr.util.FastWeightedList;
import com.wanderersoftherift.wotr.util.RandomSourceFromJavaRandom;
import com.wanderersoftherift.wotr.util.Ref;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.util.ProcessorUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.Optional;

public class AnomalyProcessor extends StructureProcessor implements RiftTemplateProcessor {

    public static final MapCodec<AnomalyProcessor> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    FastWeightedList.codec(AnomalyTask.HOLDER_CODEC)
                            .fieldOf("tasks")
                            .forGetter(AnomalyProcessor::getTasks),
                    FastWeightedList.codec(AnomalyReward.HOLDER_CODEC)
                            .fieldOf("rewards")
                            .forGetter(AnomalyProcessor::getRewards),
                    ResourceLocation.CODEC.optionalFieldOf("anomaly_panorama_texture")
                            .forGetter(AnomalyProcessor::getPanorama),
                    RegistryCodecs.homogeneousList(Registries.BLOCK)
                            .fieldOf("replace_with_anomaly")
                            .forGetter(AnomalyProcessor::replaced)
            ).apply(instance, AnomalyProcessor::new));

    private final Optional<ResourceLocation> panorama;
    private final FastWeightedList<Holder<AnomalyTask<?>>> tasks;
    private final FastWeightedList<Holder<AnomalyReward>> rewards;
    private final HolderSet<Block> replaced;

    public AnomalyProcessor(FastWeightedList<Holder<AnomalyTask<?>>> tasks,
            FastWeightedList<Holder<AnomalyReward>> rewards, Optional<ResourceLocation> panorama,
            HolderSet<Block> replaced) {
        this.panorama = panorama;
        this.tasks = tasks;
        this.rewards = rewards;
        this.replaced = replaced;
    }

    @Override
    public StructureTemplate.StructureBlockInfo process(
            LevelReader world,
            BlockPos piecePos,
            BlockPos structurePos,
            StructureTemplate.StructureBlockInfo rawBlockInfo,
            StructureTemplate.StructureBlockInfo blockInfo,
            StructurePlaceSettings settings,
            StructureTemplate template) {
        return backportProcess(world, piecePos, structurePos, rawBlockInfo, blockInfo, settings, template);
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
        if (!replaced.contains(currentState.getBlockHolder())) {
            return currentState;
        }
        var position = new BlockPos(x, y, z);

        var newState = WotrBlocks.ANOMALY.get().defaultBlockState();
        if (currentState.hasProperty(BlockStateProperties.FACING)) {
            newState = newState.setValue(AnomalyBlock.FACING, currentState.getValue(BlockStateProperties.FACING));
        }
        currentState = newState;
        var blockEntity = new AnomalyBlockEntity(position, currentState);
        var rng = new RandomSourceFromJavaRandom(RandomSourceFromJavaRandom.get(0),
                ProcessorUtil.getRandomSeed(position, ((WorldGenLevel) world).getSeed() + 9996554987L));
        var task = tasks.random(rng);
        var reward = rewards.random(rng);

        blockEntity.setSeed(rng.nextLong());
        blockEntity.setAnomalyState(
                new AnomalyBlockEntity.AnomalyState<>((Holder<AnomalyTask<Object>>) (Object) task, Optional.empty()));
        blockEntity.setAnomalyReward(reward);
        if (panorama.isPresent()) {
            blockEntity.setPanorama(panorama.get());
        }

        entityRef.setValue(blockEntity);

        return currentState;
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return WotrProcessors.ANOMALY.get();
    }

    private Optional<ResourceLocation> getPanorama() {
        return panorama;
    }

    public FastWeightedList<Holder<AnomalyTask<?>>> getTasks() {
        return tasks;
    }

    public FastWeightedList<Holder<AnomalyReward>> getRewards() {
        return rewards;
    }

    public HolderSet<Block> replaced() {
        return replaced;
    }
}
