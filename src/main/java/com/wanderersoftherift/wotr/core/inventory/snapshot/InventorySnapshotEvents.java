package com.wanderersoftherift.wotr.core.inventory.snapshot;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.rift.RiftData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = WanderersOfTheRift.MODID, bus = EventBusSubscriber.Bus.GAME)
public class InventorySnapshotEvents {
    @SubscribeEvent
    private static void onDropsFromDeath(LivingDropsEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            InventorySnapshotSystem.retainSnapshotItemsOnDeath(player, event);
        }
    }

    @SubscribeEvent
    private static void onPlayerDeath(PlayerEvent.PlayerRespawnEvent event) {
        if (!event.isEndConquered() && event.getEntity() instanceof ServerPlayer player) {
            InventorySnapshotSystem.restoreItemsOnRespawn(player);
        }
    }

    @SubscribeEvent
    private static void onPlayerEnterDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (!(event.getEntity().level() instanceof ServerLevel level)
                || !(event.getEntity() instanceof ServerPlayer serverPlayer)) {
            return;
        }
        boolean fromRift = RiftData.isRift(level.getServer().getLevel(event.getFrom()));
        boolean toRift = RiftData.isRift(level.getServer().getLevel(event.getTo()));
        if (!fromRift && toRift) {
            InventorySnapshotSystem.captureSnapshot(serverPlayer);
        } else if (fromRift && !toRift) {
            InventorySnapshotSystem.clearSnapshot(serverPlayer);
        }
    }
}
