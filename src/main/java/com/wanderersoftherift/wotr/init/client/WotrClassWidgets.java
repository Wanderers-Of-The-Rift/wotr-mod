package com.wanderersoftherift.wotr.init.client;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.guild.quest.goal.GiveItemGoal;
import com.wanderersoftherift.wotr.core.guild.quest.reward.ItemReward;
import com.wanderersoftherift.wotr.gui.widget.GiveItemGoalWidget;
import com.wanderersoftherift.wotr.gui.widget.ItemRewardWidget;
import com.wanderersoftherift.wotr.gui.widget.lookup.RegisterClassWidgetsEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = WanderersOfTheRift.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class WotrClassWidgets {
    @SubscribeEvent
    private static void registerWidgets(RegisterClassWidgetsEvent event) {
        event.register(GiveItemGoal.class, GiveItemGoalWidget::new);
        event.register(ItemReward.class, ItemRewardWidget::new);
    }
}
