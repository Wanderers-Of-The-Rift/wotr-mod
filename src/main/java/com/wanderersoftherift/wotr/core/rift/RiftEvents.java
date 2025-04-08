package com.wanderersoftherift.wotr.core.rift;

import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@EventBusSubscriber
public class RiftEvents {
    @SubscribeEvent
    public static void onRiftTick(LevelTickEvent.Post event) {
        if (event.getLevel() instanceof ServerLevel serverLevel) {
            if (!RiftData.isRift(serverLevel)) {return;}
            var data = RiftData.get(serverLevel);
             var toRemove = new HashSet<UUID>();
            for (var playerID : data.getPlayers()) {
                var player = serverLevel.getServer().getPlayerList().getPlayer(playerID);
                if (player == null) {
                    continue; // Player is offline
                }
                if (player.isDeadOrDying()) {
                    toRemove.add(playerID);
                }
                if (player.level() instanceof ServerLevel pLevel && !RiftData.isRift(pLevel) && pLevel != serverLevel) {
                    // prevent tp out of the rift, only allow exiting though portal
                    player.teleportTo(serverLevel, 5, 0, 5, Set.of(), player.getYRot(), player.getXRot(), false);
                }
            }
            for (var playerID : toRemove) {
                data.removePlayer(playerID);
            }
            if (data.getPlayers().isEmpty()) {
                RiftLevelManager.unregisterAndDeleteLevel(serverLevel);
            }
        }
    }
}
