package com.wanderersoftherift.wotr.entity.portal;

import com.wanderersoftherift.wotr.core.rift.RiftLevelManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * This entity provides the exit from a rift back to the world of origin
 */
public class RiftPortalExitEntity extends RiftPortalEntity {

    public RiftPortalExitEntity(EntityType<? extends RiftPortalExitEntity> entityType, Level level) {
        super(entityType, level);
        blocksBuilding = true;
    }

    @Override
    protected void onPlayerInPortal(Player player, ServerLevel riftLevel) {
        RiftLevelManager.returnPlayerFromRift(player);
    }

}
