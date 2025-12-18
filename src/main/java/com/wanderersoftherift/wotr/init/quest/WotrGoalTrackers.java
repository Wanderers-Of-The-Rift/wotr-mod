package com.wanderersoftherift.wotr.init.quest;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.goal.RegisterGoalTrackerEvent;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = WanderersOfTheRift.MODID, bus = EventBusSubscriber.Bus.MOD)
public class WotrGoalTrackers {

    @SubscribeEvent
    public static void registerGoalTrackers(RegisterGoalTrackerEvent event) {
        event.register(player -> player.getExistingData(WotrAttachments.ACTIVE_QUESTS).stream());
        event.register(player -> player.level().getExistingData(WotrAttachments.OBJECTIVE_DATA).stream());
    }
}
