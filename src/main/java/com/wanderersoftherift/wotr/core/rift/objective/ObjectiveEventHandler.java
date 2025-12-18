package com.wanderersoftherift.wotr.core.rift.objective;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.network.rift.S2CRiftObjectiveStatusPacket;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Optional;

@EventBusSubscriber(modid = WanderersOfTheRift.MODID)
public class ObjectiveEventHandler {

    @SubscribeEvent
    public static void onLivingDeathEvent(LivingDeathEvent event) {
        if (event.getEntity().level() instanceof ServerLevel serverLevel) {
            Optional<OngoingObjective> ongoingObjective = serverLevel.getExistingData(WotrAttachments.OBJECTIVE_DATA)
                    .flatMap(ObjectiveData::getObjective);
            ongoingObjective.ifPresent(objective -> {
                if (objective.onLivingDeath(event, serverLevel)) {
                    broadcastObjectiveStatus(serverLevel, ongoingObjective);
                }
            });
        }
    }

    private static void broadcastObjectiveStatus(ServerLevel serverLevel, Optional<OngoingObjective> objective) {
        PacketDistributor.sendToPlayersInDimension(serverLevel, new S2CRiftObjectiveStatusPacket(objective));
    }
}
