package com.wanderersoftherift.wotr.core.rift;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.Set;

@EventBusSubscriber
public class RiftEvents {

    @SubscribeEvent
    public static void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        ServerLevel originLevel = RiftLevelManager.getRiftLevel(event.getFrom().location());
        if (originLevel == null) {
            return;
        }
        RiftData riftData = RiftData.get(originLevel);
        if (riftData.containsPlayer(event.getEntity())) {
            event.getEntity()
                    .teleportTo(originLevel, 5, 0, 5, Set.of(), event.getEntity().getYRot(),
                            event.getEntity().getXRot(), false);
        }
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            RiftLevelManager.onPlayerDeath(player, player.serverLevel());
        }
    }
}
