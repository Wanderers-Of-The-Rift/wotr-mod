package com.wanderersoftherift.wotr.world.level.levelgen.processor;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.block.RiftMobSpawnerBlock;
import com.wanderersoftherift.wotr.block.blockentity.RiftMobSpawnerBlockEntity;
import com.wanderersoftherift.wotr.init.WotrBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.TrialSpawnerBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.trialspawner.PlayerDetector;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawner;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawnerConfig;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawnerData;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawnerState;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import static com.wanderersoftherift.wotr.block.blockentity.RiftMobSpawnerBlockEntity.RIFT_PLAYERS;
import static com.wanderersoftherift.wotr.init.worldgen.WotrProcessors.TRIAL_SPAWNER;

public class TrialSpawnerProcessor extends StructureProcessor {
    public static final MapCodec<TrialSpawnerProcessor> CODEC = RecordCodecBuilder.mapCodec(builder -> builder
            .group(TrialSpawnerConfig.CODEC.fieldOf("config").forGetter(TrialSpawnerProcessor::getSpawnerConfig)
            ).apply(builder, TrialSpawnerProcessor::new));

    private final Holder<TrialSpawnerConfig> spawnerConfig;

    public TrialSpawnerProcessor(Holder<TrialSpawnerConfig> spawnerConfig) {
        this.spawnerConfig = spawnerConfig;
    }

    public Holder<TrialSpawnerConfig> getSpawnerConfig() {
        return spawnerConfig;
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
                        blockState.setValue(RiftMobSpawnerBlock.STATE, TrialSpawnerState.INACTIVE),
                        getBlockEntity(world, spawnerBlockEntity));
            }
        }
        if (blockInfo.state().getBlock() instanceof RiftMobSpawnerBlock) {
            BlockEntity blockEntity = ((RiftMobSpawnerBlock) blockInfo.state().getBlock())
                    .newBlockEntity(blockInfo.pos(), blockInfo.state());
            if (blockEntity instanceof RiftMobSpawnerBlockEntity spawnerBlockEntity) {
                return new StructureTemplate.StructureBlockInfo(blockInfo.pos(),
                        blockInfo.state().setValue(RiftMobSpawnerBlock.STATE, TrialSpawnerState.INACTIVE),
                        getBlockEntity(world, spawnerBlockEntity));
            }
        }
        return blockInfo;
    }

    private CompoundTag getBlockEntity(LevelReader world, RiftMobSpawnerBlockEntity blockEntity) {
        TrialSpawner trialSpawner = new TrialSpawner(
                spawnerConfig, spawnerConfig, new TrialSpawnerData(), 72_000, 9, blockEntity, RIFT_PLAYERS,
                PlayerDetector.EntitySelector.SELECT_FROM_LEVEL);
        trialSpawner.getData().reset();
        blockEntity.setTrialSpawner(trialSpawner);
        return blockEntity.saveWithId(world.registryAccess());
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return TRIAL_SPAWNER.get();
    }
}
