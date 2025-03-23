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
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Random;
import java.util.Set;

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
            for (Entity player : serverLevel.getEntities(this, makeBoundingBox(), x -> x instanceof ServerPlayer serverPlayer)) {
                if (player instanceof ServerPlayer serverPlayer) {
                    if (RiftData.isRift(serverLevel)) {
                        tpHome(serverPlayer, serverLevel);
                        continue;
                    }
                    tpToRift(serverPlayer, serverLevel, new BlockPos(blockPosition().getX(), blockPosition().getY(), blockPosition().getZ()));
                }
            }
        }
    }


    private static InteractionResult tpToRift(ServerPlayer player, ServerLevel level, BlockPos pos) {
        ResourceLocation riftId = WanderersOfTheRift.id("rift_" + pos.getX() + "_" + pos.getY() + "_" + pos.getZ());
        var plDir = player.getDirection().getOpposite();
        var axis = plDir.getAxis();
        var axisDir = plDir.getAxisDirection().getStep();


        ServerLevel lvl = RiftLevelManager.getOrCreateRiftLevel(riftId, level.dimension(), pos.relative(axis, 3 * axisDir));
        if (lvl == null) {
            player.displayClientMessage(Component.literal("Failed to create rift"), true);
            return InteractionResult.FAIL;
        }
        RiftData.get(lvl).addPlayer(player.getUUID());

        var riftSpawnCoords = getRiftSpawnCoords();

        player.teleportTo(lvl, riftSpawnCoords.x, riftSpawnCoords.y, riftSpawnCoords.z, Set.of(), player.getYRot(), 0, false);
        NeoForge.EVENT_BUS.post(new PlayerEvent.PlayerChangedDimensionEvent(player, level.dimension(), lvl.dimension()));
        return InteractionResult.SUCCESS;
    }

    private static Vec3 getRiftSpawnCoords(){
        var random = new Random();
        double x = random.nextDouble(2,4);
        double y = 0;
        double z = random.nextDouble(2,4);
        if (random.nextBoolean()) {
            x = -x;
        }
        if (random.nextBoolean()) {
            z = -z;
        }

        return new Vec3(x, y, z);
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
        serverPlayer.teleportTo(respawnDimension, respawnPos.getCenter().x(), respawnPos.getY(), respawnPos.getCenter().z(), Set.of(), serverPlayer.getRespawnAngle(), 0, true);
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
