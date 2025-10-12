package com.wanderersoftherift.wotr.entity.portal;

import com.wanderersoftherift.wotr.init.WotrEntityDataSerializers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import static com.wanderersoftherift.wotr.core.rift.RiftLevelManager.levelExists;

/**
 * This entity provides the entrance into a rift.
 */
public class RiftPortalEntranceEntity extends RiftPortalEntity {
    private static final EntityDataAccessor<RiftEntrance> DATA_RIFT_ENTRANCE = SynchedEntityData
            .defineId(RiftPortalEntranceEntity.class, WotrEntityDataSerializers.RIFT_ENTRANCE.get());

    public RiftPortalEntranceEntity(EntityType<? extends RiftPortalEntranceEntity> entityType, Level level) {
        super(entityType, level);
        blocksBuilding = true;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_RIFT_ENTRANCE, RiftEntrance.create());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setEntrance(RiftEntrance.loadRiftEntrance(tag, level().registryAccess()));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        entrance().saveRiftEntrance(tag, level().registryAccess());
    }

    @Override
    public void tick() {
        if (!level().isClientSide && entrance().generated() && !levelExists(entrance().target())) {
            this.remove(RemovalReason.DISCARDED);
            return;
        }
        super.tick();
    }

    @Override
    protected void onPlayerInPortal(ServerPlayer player, ServerLevel level) {
        var entrance = entrance();
        var generated = entrance.teleportPlayer(player, level, blockPosition());
        if (generated != entrance.generated()) {
            setEntrance(new RiftEntrance(entrance.keyItem(), entrance.target(), generated));
        }
    }

    public RiftEntrance entrance() {
        return entityData.get(DATA_RIFT_ENTRANCE);
    }

    public void setEntrance(RiftEntrance entrance) {
        entityData.set(DATA_RIFT_ENTRANCE, entrance);
    }

    public void setKeyItem(ItemStack riftKey) {
        var entrance = entrance();
        setEntrance(new RiftEntrance(riftKey, entrance.target(), entrance.generated()));
    }
}
