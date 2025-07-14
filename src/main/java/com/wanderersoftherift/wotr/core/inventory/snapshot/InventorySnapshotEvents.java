package com.wanderersoftherift.wotr.core.inventory.snapshot;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.rift.RiftParticipation;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.stream.Collectors;

@EventBusSubscriber(modid = WanderersOfTheRift.MODID, bus = EventBusSubscriber.Bus.GAME)
public class InventorySnapshotEvents {
    @SubscribeEvent
    private static void onDropsFromDeath(LivingDropsEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            var deathParticipation = player.getData(WotrAttachments.DIED_IN_RIFT);
            if (deathParticipation != RiftParticipation.EMPTY) {
                var remainingParticipations = player.getData(WotrAttachments.PARTICIPATIONS);
                InventorySnapshotSystem.retainSnapshotItemsOnDeath(player, event,
                        deathParticipation.entranceInventory(),
                        remainingParticipations.stream().map(it -> it.entranceInventory()).toList());
            }
        }
    }

    @SubscribeEvent
    private static void onPlayerDeath(PlayerEvent.PlayerRespawnEvent event) {
        if (!event.isEndConquered() && event.getEntity() instanceof ServerPlayer player) {
            InventorySnapshotSystem.restoreItemsOnRespawn(player,
                    player.getData(WotrAttachments.PARTICIPATIONS)
                            .stream()
                            .map(it -> it.entranceInventory().id())
                            .collect(Collectors.toSet()));
        }
    }

}
