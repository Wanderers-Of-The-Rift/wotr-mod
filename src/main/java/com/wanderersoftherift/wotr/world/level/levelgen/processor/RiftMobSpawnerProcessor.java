package com.wanderersoftherift.wotr.world.level.levelgen.processor;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.block.RiftMobSpawnerBlock;
import com.wanderersoftherift.wotr.block.blockentity.RiftMobSpawnerBlockEntity;
import com.wanderersoftherift.wotr.block.blockentity.riftmobspawner.RiftMobSpawner;
import com.wanderersoftherift.wotr.block.blockentity.riftmobspawner.RiftMobSpawnerData;
import com.wanderersoftherift.wotr.block.blockentity.riftmobspawner.RiftMobSpawnerState;
import com.wanderersoftherift.wotr.init.WotrBlocks;
import com.wanderersoftherift.wotr.util.Ref;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.TrialSpawnerBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.trialspawner.PlayerDetector;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawnerConfig;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.Optional;

import static com.wanderersoftherift.wotr.block.blockentity.RiftMobSpawnerBlockEntity.RIFT_PLAYERS;
import static com.wanderersoftherift.wotr.init.worldgen.WotrProcessors.RIFT_MOB_SPAWNER;

public class RiftMobSpawnerProcessor extends StructureProcessor implements RiftTemplateProcessor {
    public static final MapCodec<RiftMobSpawnerProcessor> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            TrialSpawnerConfig.CODEC.optionalFieldOf("config").forGetter(RiftMobSpawnerProcessor::getSpawnerConfig),
            TrialSpawnerConfig.CODEC.optionalFieldOf("ominous_config")
                    .forGetter(RiftMobSpawnerProcessor::getSpawnerConfig)
    ).apply(builder, RiftMobSpawnerProcessor::new));

    private final Optional<Holder<TrialSpawnerConfig>> spawnerConfig;
    private final Optional<Holder<TrialSpawnerConfig>> ominousConfig;

    public RiftMobSpawnerProcessor(Optional<Holder<TrialSpawnerConfig>> spawnerConfig,
            Optional<Holder<TrialSpawnerConfig>> ominousConfig) {
        this.spawnerConfig = spawnerConfig;
        this.ominousConfig = ominousConfig;
    }

    public Optional<Holder<TrialSpawnerConfig>> getSpawnerConfig() {
        return spawnerConfig;
    }

    public Optional<Holder<TrialSpawnerConfig>> getOminousConfig() {
        return ominousConfig;
    }

    @Override
    public StructureTemplate.StructureBlockInfo process(
            LevelReader world,
            BlockPos piecePos,
            BlockPos structurePos,
            StructureTemplate.StructureBlockInfo rawBlockInfo,
            StructureTemplate.StructureBlockInfo blockInfo,
            StructurePlaceSettings settings,
            @javax.annotation.Nullable StructureTemplate template) {
        if (blockInfo.state().getBlock() instanceof TrialSpawnerBlock) {
            BlockState blockState = WotrBlocks.RIFT_MOB_SPAWNER.get().defaultBlockState();
            BlockEntity blockEntity = ((RiftMobSpawnerBlock) blockState.getBlock()).newBlockEntity(blockInfo.pos(),
                    blockState);
            if (blockEntity instanceof RiftMobSpawnerBlockEntity spawnerBlockEntity) {
                return new StructureTemplate.StructureBlockInfo(blockInfo.pos(),
                        blockState.setValue(RiftMobSpawnerBlock.STATE, RiftMobSpawnerState.INACTIVE),
                        getBlockEntity(world, spawnerBlockEntity));
            }
        }
        if (blockInfo.state().getBlock() instanceof RiftMobSpawnerBlock) {
            BlockEntity blockEntity = ((RiftMobSpawnerBlock) blockInfo.state().getBlock())
                    .newBlockEntity(blockInfo.pos(), blockInfo.state());
            if (blockEntity instanceof RiftMobSpawnerBlockEntity spawnerBlockEntity) {
                return new StructureTemplate.StructureBlockInfo(blockInfo.pos(),
                        blockInfo.state().setValue(RiftMobSpawnerBlock.STATE, RiftMobSpawnerState.INACTIVE),
                        getBlockEntity(world, spawnerBlockEntity));
            }
        }
        return blockInfo;
    }

    private CompoundTag getBlockEntity(LevelReader world, RiftMobSpawnerBlockEntity blockEntity) {
        Holder<TrialSpawnerConfig> normalConfig = getFinalNormalConfig(world);
        Holder<TrialSpawnerConfig> ominousConfig = getFinalOminousConfig(normalConfig);
        RiftMobSpawner riftMobSpawner = new RiftMobSpawner(
                normalConfig, ominousConfig, new RiftMobSpawnerData(), 72_000, 9, blockEntity, RIFT_PLAYERS,
                PlayerDetector.EntitySelector.SELECT_FROM_LEVEL);
        riftMobSpawner.getData().reset();
        blockEntity.setRiftMobSpawner(riftMobSpawner);
        return blockEntity.saveWithId(world.registryAccess());
    }

    private Holder<TrialSpawnerConfig> getFinalNormalConfig(LevelReader world) {
        return spawnerConfig.orElseGet(() -> world.registryAccess()
                .lookupOrThrow(Registries.TRIAL_SPAWNER_CONFIG)
                .get(WanderersOfTheRift.id("rift"))
                .orElseThrow());
    }

    private Holder<TrialSpawnerConfig> getFinalOminousConfig(Holder<TrialSpawnerConfig> normalConfig) {
        return ominousConfig.orElse(normalConfig);
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return RIFT_MOB_SPAWNER.get();
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
        Holder<TrialSpawnerConfig> normalConfig = getFinalNormalConfig(world);
        Holder<TrialSpawnerConfig> ominousConfig = getFinalOminousConfig(normalConfig);

        if (currentState.getBlock() instanceof TrialSpawnerBlock) {
            var riftSpawnerBlock = WotrBlocks.RIFT_MOB_SPAWNER.get();
            BlockState blockState = riftSpawnerBlock.defaultBlockState()
                    .setValue(RiftMobSpawnerBlock.STATE, RiftMobSpawnerState.INACTIVE);
            ;

            var newBlockEntity = (RiftMobSpawnerBlockEntity) riftSpawnerBlock.newBlockEntity(new BlockPos(x, y, z),
                    blockState);
            RiftMobSpawner riftMobSpawner = new RiftMobSpawner(
                    normalConfig, ominousConfig, new RiftMobSpawnerData(), 72_000, 9, newBlockEntity, RIFT_PLAYERS,
                    PlayerDetector.EntitySelector.SELECT_FROM_LEVEL);
            riftMobSpawner.getData().reset();
            newBlockEntity.setRiftMobSpawner(riftMobSpawner);
            entityRef.setValue(newBlockEntity);
            return blockState;

        }

        if (currentState.getBlock() instanceof RiftMobSpawnerBlock riftSpawnerBlock) {
            RiftMobSpawner riftMobSpawner;
            RiftMobSpawnerBlockEntity newBlockEntity;
            if (entityRef.getValue() instanceof RiftMobSpawnerBlockEntity spawnerBlockEntity) {
                riftMobSpawner = new RiftMobSpawner(
                        normalConfig, ominousConfig, new RiftMobSpawnerData(), 72_000, 9, spawnerBlockEntity,
                        RIFT_PLAYERS, PlayerDetector.EntitySelector.SELECT_FROM_LEVEL);
                newBlockEntity = spawnerBlockEntity;
            } else {
                newBlockEntity = (RiftMobSpawnerBlockEntity) riftSpawnerBlock.newBlockEntity(new BlockPos(x, y, z),
                        currentState);
                riftMobSpawner = new RiftMobSpawner(
                        normalConfig, ominousConfig, new RiftMobSpawnerData(), 72_000, 9, newBlockEntity, RIFT_PLAYERS,
                        PlayerDetector.EntitySelector.SELECT_FROM_LEVEL);
                entityRef.setValue(newBlockEntity);
            }
            riftMobSpawner.getData().reset();
            newBlockEntity.setRiftMobSpawner(riftMobSpawner);
            currentState = currentState.setValue(RiftMobSpawnerBlock.STATE, RiftMobSpawnerState.INACTIVE);
        }
        return currentState;
    }
}
