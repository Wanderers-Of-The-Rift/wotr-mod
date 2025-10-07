package com.wanderersoftherift.wotr.rift.objective;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.rift.RiftData;
import com.wanderersoftherift.wotr.network.rift.S2CRiftObjectiveStatusPacket;
import com.wanderersoftherift.wotr.rift.objective.ongoing.CollectOngoingObjective;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = WanderersOfTheRift.MODID)
public class ObjectiveEventWrapper {

    @SubscribeEvent
    public static void onLivingDeathEvent(LivingDeathEvent event) {
        if (event.getEntity().level() instanceof ServerLevel serverLevel) {
            var data = RiftData.get(serverLevel);
            if (data.getObjective().isPresent()) {
                boolean dirty = data.getObjective().get().onLivingDeath(event, serverLevel, data);
                if (dirty) {
                    data.setDirty();
                    broadcastObjectiveStatus(serverLevel, data);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onItemPickup(ItemEntityPickupEvent.Post event) {
        var player = event.getPlayer();
        if (!(player.level() instanceof ServerLevel serverLevel)) return;

        var data = RiftData.get(serverLevel);
        var ongoing = data.getObjective().orElse(null);
        if (ongoing instanceof CollectOngoingObjective co) {
            if (co.onInventoryCheck(player)) {
                data.setDirty();
                broadcastObjectiveStatus(serverLevel, data);
            }
        }
    }

    @SubscribeEvent
    public static void onContainerClose(PlayerContainerEvent.Close event) {
        var player = event.getEntity();
        if (!(player.level() instanceof ServerLevel serverLevel)) return;

        var data = RiftData.get(serverLevel);
        var ongoing = data.getObjective().orElse(null);
        if (ongoing instanceof CollectOngoingObjective co) {
            if (co.onInventoryCheck(player)) {
                data.setDirty();
                broadcastObjectiveStatus(serverLevel, data);
            }
        }
    }

    private static void broadcastObjectiveStatus(ServerLevel serverLevel, RiftData data) {
        PacketDistributor.sendToPlayersInDimension(serverLevel, new S2CRiftObjectiveStatusPacket(data.getObjective()));
    }
}
