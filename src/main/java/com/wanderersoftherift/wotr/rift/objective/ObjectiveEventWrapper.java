package com.wanderersoftherift.wotr.rift.objective;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.rift.RiftData;
import com.wanderersoftherift.wotr.network.rift.S2CRiftObjectiveStatusPacket;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
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

    private static void broadcastObjectiveStatus(ServerLevel serverLevel, RiftData data) {
        PacketDistributor.sendToPlayersInDimension(serverLevel, new S2CRiftObjectiveStatusPacket(data.getObjective()));
    }
}
