package com.wanderersoftherift.wotr.core.inventory.snapshot;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.rift.RiftEntryState;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = WanderersOfTheRift.MODID, bus = EventBusSubscriber.Bus.GAME)
public class InventorySnapshotEvents {
    @SubscribeEvent
    private static void onDropsFromDeath(LivingDropsEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        var deathRiftEntryState = player.getData(WotrAttachments.DEATH_RIFT_ENTRY_STATE);
        if (deathRiftEntryState == RiftEntryState.EMPTY) {
            return;
        }
        var remainingRiftEntryStates = player.getData(WotrAttachments.RIFT_ENTRY_STATES);
        InventorySnapshotSystem.retainSnapshotItemsOnDeath(player, event, deathRiftEntryState.entranceInventory(),
                remainingRiftEntryStates.stream().map(RiftEntryState::entranceInventory).toList());
    }

    @SubscribeEvent
    private static void onPlayerDeath(PlayerEvent.PlayerRespawnEvent event) {
        if (event.isEndConquered() || !(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        InventorySnapshotSystem.restoreItemsOnRespawn(player, InventorySnapshotSystem.snapshotsToIdSet(
                player.getData(WotrAttachments.RIFT_ENTRY_STATES).stream().map(RiftEntryState::entranceInventory)));
    }
}
