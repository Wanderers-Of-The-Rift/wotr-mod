package com.wanderersoftherift.wotr.entity.portal;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.rift.RiftData;
import com.wanderersoftherift.wotr.core.rift.RiftLevelManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;

/**
 * This entity provides the entrance into a rift.
 */
public class RiftPortalExitEntity extends Entity {
    private static final String BILLBOARD = "billboard";
    private static final EntityDataAccessor<Boolean> DATA_BILLBOARD = SynchedEntityData
            .defineId(RiftPortalExitEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<ItemStack> DATA_RIFTKEY = SynchedEntityData
            .defineId(RiftPortalExitEntity.class, EntityDataSerializers.ITEM_STACK);

    private boolean generated = false;
    private ResourceLocation riftDimensionID = WanderersOfTheRift.id("rift_" + UUID.randomUUID());

    public RiftPortalExitEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
        blocksBuilding = true;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_BILLBOARD, true).define(DATA_RIFTKEY, ItemStack.EMPTY);
    }

    public ResourceLocation getRiftDimensionID() {
        return riftDimensionID;
    }

    public void setRiftDimensionID(ResourceLocation riftDimensionID) {
        this.riftDimensionID = riftDimensionID;
    }

    public boolean isGenerated() {
        return generated;
    }

    @Override
    public void tick() {
        super.tick();
        if (level() instanceof ServerLevel serverLevel) {
            for (Entity player : serverLevel.getEntities(this, makeBoundingBox(), x -> x instanceof ServerPlayer)) {
                if (player instanceof ServerPlayer serverPlayer) {
                    tpHome(serverPlayer, serverLevel);
                }
            }
        }
    }

    private static InteractionResult tpHome(ServerPlayer serverPlayer, ServerLevel riftLevel) {
        ResourceKey<Level> respawnKey = RiftData.get(riftLevel).getPortalDimension();
        if (respawnKey == riftLevel.dimension()) {
            respawnKey = Level.OVERWORLD;
        }
        ServerLevel respawnDimension = riftLevel.getServer().getLevel(respawnKey);
        if (respawnDimension == null) {
            respawnDimension = riftLevel.getServer().overworld();
        }
        var respawnPos = RiftData.get(riftLevel).getPortalPos().above();
        serverPlayer.teleportTo(respawnDimension, respawnPos.getCenter().x(), respawnPos.getY(),
                respawnPos.getCenter().z(), Set.of(), serverPlayer.getRespawnAngle(), 0, true);
        RiftData.get(riftLevel).removePlayer(serverPlayer.getUUID());
        RiftLevelManager.unregisterAndDeleteLevel(riftLevel);
        return InteractionResult.SUCCESS;
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
        if (tag.contains("riftKey")) {
            setRiftkey(ItemStack.parseOptional(this.level().registryAccess(), tag.getCompound("riftKey")));
        }
        if (tag.contains("riftDimensionID")) {
            setRiftDimensionID(ResourceLocation.parse(tag.getString("riftDimensionID")));
        }
        if (tag.contains("generated")) {
            generated = tag.getBoolean("generated");
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putBoolean(BILLBOARD, isBillboard());
        tag.put("riftKey", getRiftKey().save(this.level().registryAccess(), new CompoundTag()));
        tag.putString("riftDimensionID", getRiftDimensionID().toString());
        tag.putBoolean("generated", generated);
    }

    public void setBillboard(boolean billboard) {
        this.entityData.set(DATA_BILLBOARD, billboard);
    }

    public boolean isBillboard() {
        return entityData.get(DATA_BILLBOARD);
    }

    public void setRiftkey(ItemStack key) {
        this.entityData.set(DATA_RIFTKEY, key);
    }

    public ItemStack getRiftKey() {
        return entityData.get(DATA_RIFTKEY);
    }

}
