package com.wanderersoftherift.wotr.entity.portal;

import com.wanderersoftherift.wotr.core.rift.RiftData;
import com.wanderersoftherift.wotr.core.rift.RiftLevelManager;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

import java.util.Set;

/**
 * This entity provides the exit from a rift back to the world of origin
 */
public class RiftPortalExitEntity extends RiftPortalEntity {

    public RiftPortalExitEntity(EntityType<? extends RiftPortalExitEntity> entityType, Level level) {
        super(entityType, level);
        blocksBuilding = true;
    }

    @Override
    protected void onPlayerInPortal(ServerPlayer serverPlayer, ServerLevel riftLevel) {
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
        // TODO: Possibly a better way to weave these functions together (possibly remove player via rift level manager
        // so it can check and unregister+delete level?
        RiftData.get(riftLevel).removePlayer(serverPlayer.getUUID());
        RiftLevelManager.unregisterAndDeleteLevel(riftLevel);
    }

}
