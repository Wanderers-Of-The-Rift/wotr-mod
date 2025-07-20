package com.wanderersoftherift.wotr.entity.portal;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.rift.RiftData;
import com.wanderersoftherift.wotr.core.rift.RiftLevelManager;
import com.wanderersoftherift.wotr.core.rift.stats.StatSnapshot;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.init.WotrEntityDataSerializers;
import com.wanderersoftherift.wotr.item.riftkey.RiftConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Random;
import java.util.Set;
import java.util.UUID;

import static com.wanderersoftherift.wotr.core.rift.RiftLevelManager.levelExists;

/**
 * This entity provides the entrance into a rift.
 */
public class RiftPortalEntranceEntity extends RiftPortalEntity {
    private static final EntityDataAccessor<RiftConfig> DATA_RIFT_CONFIG = SynchedEntityData
            .defineId(RiftPortalEntranceEntity.class, WotrEntityDataSerializers.RIFT_CONFIG_SERIALIZER.get());
    private static final EntityDataAccessor<String> DATA_RIFT_ID = SynchedEntityData
            .defineId(RiftPortalEntranceEntity.class, EntityDataSerializers.STRING);

    private boolean generated = false;

    public RiftPortalEntranceEntity(EntityType<? extends RiftPortalEntranceEntity> entityType, Level level) {
        super(entityType, level);
        blocksBuilding = true;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_RIFT_CONFIG, new RiftConfig(0));
        builder.define(DATA_RIFT_ID, WanderersOfTheRift.id("rift_" + UUID.randomUUID()).toString());
    }

    public ResourceLocation getRiftDimensionId() {
        return ResourceLocation.parse(entityData.get(DATA_RIFT_ID));
    }

    private void setRiftDimensionId(String id) {
        if (id != null) {
            this.entityData.set(DATA_RIFT_ID, id);
        }
    }

    public boolean isGenerated() {
        return generated;
    }

    @Override
    public void tick() {
        if (generated) {
            if (!levelExists(getRiftDimensionId())) {
                this.remove(RemovalReason.DISCARDED);
                return;
            }
        }
        super.tick();
    }

    @Override
    protected void onPlayerInPortal(ServerPlayer player, ServerLevel level) {
        BlockPos pos = blockPosition();
        ResourceLocation riftId = this.getRiftDimensionId();
        var plDir = player.getDirection().getOpposite();
        var axis = plDir.getAxis();
        var axisDir = plDir.getAxisDirection().getStep();

        ServerLevel lvl = RiftLevelManager.getOrCreateRiftLevel(riftId, level.dimension(),
                pos.relative(axis, 3 * axisDir), getRiftConfig(), player);
        if (lvl == null) {
            player.displayClientMessage(Component.translatable(WanderersOfTheRift.MODID + ".rift.create.failed"), true);
            return;
        }
        generated = true;
        if (RiftData.get(lvl).isBannedFromRift(player)) {
            return;
        }

        RiftData.get(lvl).addPlayer(player.getUUID());

        var riftSpawnCoords = getRiftSpawnCoords();
        player.setData(WotrAttachments.PRE_RIFT_STATS, new StatSnapshot(player));
        player.teleportTo(lvl, riftSpawnCoords.x, riftSpawnCoords.y, riftSpawnCoords.z, Set.of(), player.getYRot(), 0,
                false);
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

        return new Vec3(PortalSpawnLocation.DEFAULT_RIFT_EXIT_POSITION).add(x, y, z);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("riftConfig")) {
            setRiftConfig(RiftConfig.CODEC
                    .parse(this.level().registryAccess().createSerializationContext(NbtOps.INSTANCE),
                            tag.getCompound("riftConfig"))
                    .resultOrPartial(x -> WanderersOfTheRift.LOGGER.error("Tried to load invalid rift config: '{}'", x))
                    .orElse(new RiftConfig(0)));
        }
        if (tag.contains("riftDimensionID")) {
            setRiftDimensionId(tag.getString("riftDimensionID"));
        }
        if (tag.contains("generated")) {
            generated = tag.getBoolean("generated");
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.put("riftConfig",
                RiftConfig.CODEC
                        .encode(getRiftConfig(),
                                this.level().registryAccess().createSerializationContext(NbtOps.INSTANCE), tag)
                        .getOrThrow());
        tag.putString("riftDimensionID", getRiftDimensionId().toString());
        tag.putBoolean("generated", generated);
    }

    public void setRiftConfig(RiftConfig config) {
        if (config != null) {
            this.entityData.set(DATA_RIFT_CONFIG, config);
        }
    }

    public RiftConfig getRiftConfig() {
        return entityData.get(DATA_RIFT_CONFIG);
    }

}
