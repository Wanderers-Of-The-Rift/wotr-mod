package com.wanderersoftherift.wotr.block.blockentity;

import com.wanderersoftherift.wotr.block.RiftMobSpawnerBlock;
import com.wanderersoftherift.wotr.init.ModBlockEntities;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Spawner;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.trialspawner.PlayerDetector;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawner;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawnerState;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import static com.wanderersoftherift.wotr.WanderersOfTheRift.LOGGER;

public class RiftMobSpawnerBlockEntity extends BlockEntity implements Spawner, TrialSpawner.StateAccessor {
    public static final PlayerDetector RIFT_PLAYERS = (
            level,
            entitySelector,
            pos,
            maxDistance,
            requiresLineOfSight) -> entitySelector.getPlayers(
                    level,
                    player -> player.blockPosition().closerThan(pos, maxDistance) && !player.isCreative()
                            && !player.isSpectator()
            ).stream().map(Entity::getUUID).toList();

    private TrialSpawner trialSpawner;

    public RiftMobSpawnerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.RIFT_MOB_SPAWNER.get(), pos, state);
        PlayerDetector.EntitySelector entityselector = PlayerDetector.EntitySelector.SELECT_FROM_LEVEL;
        this.trialSpawner = new TrialSpawner(this, RIFT_PLAYERS, entityselector);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider lookup) {
        super.loadAdditional(tag, lookup);
        this.trialSpawner.codec()
                .parse(lookup.createSerializationContext(NbtOps.INSTANCE), tag)
                .resultOrPartial(LOGGER::error)
                .ifPresent(trialSpawner -> this.trialSpawner = trialSpawner);
        if (this.level != null) {
            this.markUpdated();
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider lookup) {
        super.saveAdditional(tag, lookup);
        this.trialSpawner.codec()
                .encodeStart(lookup.createSerializationContext(NbtOps.INSTANCE), this.trialSpawner)
                .ifSuccess(success -> tag.merge((CompoundTag) success))
                .ifError(tagError -> LOGGER.warn("Failed to encode TrialSpawner {}", tagError.message()));
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider lookup) {
        return this.trialSpawner.getData().getUpdateTag(this.getBlockState().getValue(RiftMobSpawnerBlock.STATE));
    }

    @Override
    public void setEntityId(EntityType<?> entityType, RandomSource randomSource) {
        if (this.level == null) {
            Util.logAndPauseIfInIde("Expected non-null level");
        } else {
            this.trialSpawner.overrideEntityToSpawn(entityType, this.level);
            this.setChanged();
        }
    }

    public TrialSpawner getTrialSpawner() {
        return this.trialSpawner;
    }

    public void setTrialSpawner(TrialSpawner trialSpawner) {
        this.trialSpawner = trialSpawner;
    }

    @Override
    public TrialSpawnerState getState() {
        if (!this.getBlockState().hasProperty(BlockStateProperties.TRIAL_SPAWNER_STATE)) {
            return TrialSpawnerState.INACTIVE;
        } else {
            return this.getBlockState().getValue(BlockStateProperties.TRIAL_SPAWNER_STATE);
        }
    }

    @Override
    public void setState(Level level, TrialSpawnerState spawnerState) {
        this.setChanged();
        level.setBlockAndUpdate(this.worldPosition,
                this.getBlockState().setValue(BlockStateProperties.TRIAL_SPAWNER_STATE, spawnerState));
    }

    @Override
    public void markUpdated() {
        this.setChanged();
        if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
    }
}