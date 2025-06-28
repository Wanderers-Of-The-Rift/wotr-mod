package com.wanderersoftherift.wotr.init.client;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.guild.quest.goal.GiveItemGoal;
import com.wanderersoftherift.wotr.gui.widget.GiveItemGoalWidget;
import com.wanderersoftherift.wotr.gui.widget.lookup.RegisterGoalDisplaysEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = WanderersOfTheRift.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class WotrGoalDisplays {
    @SubscribeEvent
    private static void registerWidgets(RegisterGoalDisplaysEvent event) {
        event.register(GiveItemGoal.class, GiveItemGoalWidget::new);
    }
}
