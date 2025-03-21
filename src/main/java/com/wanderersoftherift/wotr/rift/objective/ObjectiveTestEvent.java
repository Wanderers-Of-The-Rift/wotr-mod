package com.wanderersoftherift.wotr.rift.objective;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;


@EventBusSubscriber(modid = WanderersOfTheRift.MODID)
public class ObjectiveTestEvent {

    @SubscribeEvent
    public static void onPlayerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event) {
        /*if(event.getEntity().level() instanceof ServerLevel serverLevel) {
            LOGGER.info("Start Objective");
            LevelRiftObjectiveData data = LevelRiftObjectiveData.getFromLevel(serverLevel);
            data.setObjective(new StealthObjective());
        }*/

    }

}
