package com.wanderersoftherift.wotr.entity.portal;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.rift.RiftData;
import com.wanderersoftherift.wotr.core.rift.RiftLevelManager;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.Set;
import java.util.UUID;

import static com.wanderersoftherift.wotr.core.rift.RiftLevelManager.isRiftExists;

/**
 * This entity provides the entrance into a rift.
 */
public class RiftPortalEntranceEntity extends Entity {
    private static final String BILLBOARD = "billboard";
    private static final EntityDataAccessor<Boolean> DATA_BILLBOARD = SynchedEntityData
            .defineId(RiftPortalEntranceEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<ItemStack> DATA_RIFTKEY = SynchedEntityData
            .defineId(RiftPortalEntranceEntity.class, EntityDataSerializers.ITEM_STACK);

    private boolean generated = false;
    private ResourceLocation riftDimensionID = WanderersOfTheRift.id("rift_" + UUID.randomUUID());

    public RiftPortalEntranceEntity(EntityType<?> entityType, Level level) {
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

    public void setGenerated(boolean generated) {
        this.generated = generated;
    }

    @Override
    public void tick() {
        super.tick();
        if (level() instanceof ServerLevel serverLevel) {
            for (Entity player : serverLevel.getEntities(this, makeBoundingBox(), x -> x instanceof ServerPlayer)) {
                if (player instanceof ServerPlayer serverPlayer) {
                    tpToRift(serverPlayer, serverLevel, blockPosition(), getRiftKey());
                }
            }
            if (generated) {
                if (!isRiftExists(getRiftDimensionID())) {
                    this.remove(RemovalReason.DISCARDED);
                }
            }
        }
    }

    private InteractionResult tpToRift(ServerPlayer player, ServerLevel level, BlockPos pos, ItemStack riftKey) {
        ResourceLocation riftId = this.getRiftDimensionID();
        var plDir = player.getDirection().getOpposite();
        var axis = plDir.getAxis();
        var axisDir = plDir.getAxisDirection().getStep();

        ServerLevel lvl = RiftLevelManager.getOrCreateRiftLevel(riftId, level.dimension(),
                pos.relative(axis, 3 * axisDir), riftKey);
        if (lvl == null) {
            player.displayClientMessage(Component.translatable(WanderersOfTheRift.MODID + ".rift.create.failed"), true);
            return InteractionResult.FAIL;
        }
        this.setGenerated(true);
        RiftData.get(lvl).addPlayer(player.getUUID());

        var riftSpawnCoords = getRiftSpawnCoords();

        player.teleportTo(lvl, riftSpawnCoords.x, riftSpawnCoords.y, riftSpawnCoords.z, Set.of(), player.getYRot(), 0,
                false);
        NeoForge.EVENT_BUS
                .post(new PlayerEvent.PlayerChangedDimensionEvent(player, level.dimension(), lvl.dimension()));
        return InteractionResult.SUCCESS;
    }

    private static Vec3 getRiftSpawnCoords() {
        var random = new Random();
        double x = random.nextDouble(2, 4);
        double y = 0;
        double z = random.nextDouble(2, 4);
        if (random.nextBoolean()) {
            x = -x;
        }
        if (random.nextBoolean()) {
            z = -z;
        }

        return new Vec3(x, y, z);
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
