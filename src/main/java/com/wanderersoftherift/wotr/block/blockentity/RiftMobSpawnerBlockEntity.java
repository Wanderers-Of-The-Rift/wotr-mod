package com.wanderersoftherift.wotr.block.blockentity;

import com.wanderersoftherift.wotr.block.RiftMobSpawnerBlock;
import com.wanderersoftherift.wotr.block.blockentity.riftmobspawner.RiftMobSpawner;
import com.wanderersoftherift.wotr.block.blockentity.riftmobspawner.RiftMobSpawnerState;
import com.wanderersoftherift.wotr.init.WotrBlockEntities;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;

import static com.wanderersoftherift.wotr.WanderersOfTheRift.LOGGER;

public class RiftMobSpawnerBlockEntity extends BlockEntity implements Spawner, RiftMobSpawner.StateAccessor {
    public static final EnumProperty<RiftMobSpawnerState> SPAWNER_STATE = EnumProperty.create("rift_mob_spawner_state",
            RiftMobSpawnerState.class);;
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

    private RiftMobSpawner riftMobSpawner;

    public RiftMobSpawnerBlockEntity(BlockPos pos, BlockState state) {
        super(WotrBlockEntities.RIFT_MOB_SPAWNER.get(), pos, state);
        PlayerDetector.EntitySelector entityselector = PlayerDetector.EntitySelector.SELECT_FROM_LEVEL;
        this.riftMobSpawner = new RiftMobSpawner(this, RIFT_PLAYERS, entityselector);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider lookup) {
        super.loadAdditional(tag, lookup);
        this.riftMobSpawner.codec()
                .parse(lookup.createSerializationContext(NbtOps.INSTANCE), tag)
                .resultOrPartial(LOGGER::error)
                .ifPresent(riftMobSpawner -> this.riftMobSpawner = riftMobSpawner);
        if (this.level != null) {
            this.markUpdated();
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider lookup) {
        super.saveAdditional(tag, lookup);
        this.riftMobSpawner.codec()
                .encodeStart(lookup.createSerializationContext(NbtOps.INSTANCE), this.riftMobSpawner)
                .ifSuccess(success -> tag.merge((CompoundTag) success))
                .ifError(tagError -> LOGGER.warn("Failed to encode RiftMobSpawner {}", tagError.message()));
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider lookup) {
        return this.riftMobSpawner.getData().getUpdateTag(this.getBlockState().getValue(RiftMobSpawnerBlock.STATE));
    }

    @Override
    public void setEntityId(EntityType<?> entityType, RandomSource randomSource) {
        if (this.level == null) {
            Util.logAndPauseIfInIde("Expected non-null level");
        } else {
            this.riftMobSpawner.overrideEntityToSpawn(entityType, this.level);
            this.setChanged();
        }
    }

    public RiftMobSpawner getRiftMobSpawner() {
        return this.riftMobSpawner;
    }

    public void setRiftMobSpawner(RiftMobSpawner riftMobSpawner) {
        this.riftMobSpawner = riftMobSpawner;
    }

    @Override
    public RiftMobSpawnerState getState() {
        if (!this.getBlockState().hasProperty(SPAWNER_STATE)) {
            return RiftMobSpawnerState.INACTIVE;
        } else {
            return this.getBlockState().getValue(SPAWNER_STATE);
        }
    }

    @Override
    public void setState(Level level, RiftMobSpawnerState spawnerState) {
        this.setChanged();
        level.setBlockAndUpdate(this.worldPosition, this.getBlockState().setValue(SPAWNER_STATE, spawnerState));
    }

    @Override
    public void markUpdated() {
        this.setChanged();
        if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
    }
}