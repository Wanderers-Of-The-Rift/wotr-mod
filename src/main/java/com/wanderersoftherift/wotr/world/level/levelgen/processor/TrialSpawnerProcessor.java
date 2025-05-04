package com.wanderersoftherift.wotr.world.level.levelgen.processor;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.mixin.TrialSpawnerBlockEntityAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.TrialSpawnerBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TrialSpawnerBlockEntity;
import net.minecraft.world.level.block.entity.trialspawner.PlayerDetector;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawner;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawnerConfig;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawnerData;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawnerState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.Nullable;

import static com.wanderersoftherift.wotr.init.ModProcessors.TRIAL_SPAWNER;

public class TrialSpawnerProcessor extends StructureProcessor {
    private static final PlayerDetector RIFT_PLAYERS = (
            level,
            entitySelector,
            pos,
            maxDistance,
            requiresLineOfSight) -> entitySelector.getPlayers(
                    level,
                    player -> player.blockPosition().closerThan(pos, maxDistance) && !player.isCreative()
                            && !player.isSpectator()
            ).stream().map(Entity::getUUID).toList();
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

    @Nullable @Override
    public StructureTemplate.StructureBlockInfo process(
            LevelReader world,
            BlockPos piecePos,
            BlockPos structurePos,
            StructureTemplate.StructureBlockInfo rawBlockInfo,
            StructureTemplate.StructureBlockInfo blockInfo,
            StructurePlaceSettings settings,
            @javax.annotation.Nullable StructureTemplate template) {
        if (blockInfo.state().getBlock() instanceof TrialSpawnerBlock) {
            BlockEntity blockEntity = ((TrialSpawnerBlock) blockInfo.state().getBlock()).newBlockEntity(blockInfo.pos(),
                    blockInfo.state());
            if (blockEntity instanceof TrialSpawnerBlockEntity trialSpawnerBlockEntity && blockInfo.nbt() != null) {
                return new StructureTemplate.StructureBlockInfo(blockInfo.pos(),
                        blockInfo.state().setValue(TrialSpawnerBlock.STATE, TrialSpawnerState.INACTIVE),
                        getBlockEntity(world, blockInfo, trialSpawnerBlockEntity));
            }
        }
        return blockInfo;
    }

    private CompoundTag getBlockEntity(
            LevelReader world,
            StructureTemplate.StructureBlockInfo blockInfo,
            TrialSpawnerBlockEntity blockEntity) {
        CompoundTag nbt = blockInfo.nbt();
        blockEntity.loadWithComponents(nbt, world.registryAccess());
        TrialSpawner trialSpawner = new TrialSpawner(
                spawnerConfig, spawnerConfig, new TrialSpawnerData(), 72_000, 9, blockEntity, RIFT_PLAYERS,
                PlayerDetector.EntitySelector.SELECT_FROM_LEVEL);
        trialSpawner.getData().reset();
        ((TrialSpawnerBlockEntityAccessor) blockEntity).setTrialSpawner(trialSpawner);
        return blockEntity.saveWithId(world.registryAccess());
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return TRIAL_SPAWNER.get();
    }
}
