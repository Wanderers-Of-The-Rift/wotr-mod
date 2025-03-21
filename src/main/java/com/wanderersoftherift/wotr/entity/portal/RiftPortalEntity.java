package com.wanderersoftherift.wotr.entity.portal;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

/**
 * This entity provides the entrance into a rift.
 */
public class RiftPortalEntity extends Entity {
    private static final String BILLBOARD = "billboard";
    private static final EntityDataAccessor<Boolean> DATA_BILLBOARD = SynchedEntityData.defineId(RiftPortalEntity.class, EntityDataSerializers.BOOLEAN);

    public RiftPortalEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
        blocksBuilding = true;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_BILLBOARD, true);
    }

    @Override
    public void tick() {
        super.tick();
        if (level() instanceof ServerLevel serverLevel) {
            for (Entity player : serverLevel.getEntities(this, makeBoundingBox(), x -> x instanceof Player)) {
                player.teleportTo(serverLevel.getServer().getLevel(Level.NETHER), 0,50,0, Collections.emptySet(), 0, 0, false);
            }
        }
    }

    @Override
    public boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource damageSource, float amount) {
        return false;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains(BILLBOARD)) {
            setBillboard(tag.getBoolean(BILLBOARD));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putBoolean(BILLBOARD, isBillboard());
    }

    public void setBillboard(boolean billboard) {
        this.entityData.set(DATA_BILLBOARD, billboard);
    }

    public boolean isBillboard() {
        return entityData.get(DATA_BILLBOARD);
    }
}
