package com.wanderersoftherift.wotr.init.client;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.goal.type.CloseAnomalyGoal;
import com.wanderersoftherift.wotr.core.goal.type.CompleteRiftGoal;
import com.wanderersoftherift.wotr.core.goal.type.GiveItemGoal;
import com.wanderersoftherift.wotr.core.goal.type.KillMobGoal;
import com.wanderersoftherift.wotr.gui.widget.lookup.RegisterGoalDisplaysEvent;
import com.wanderersoftherift.wotr.gui.widget.quest.CloseAnomalyGoalWidget;
import com.wanderersoftherift.wotr.gui.widget.quest.CompleteRiftGoalWidget;
import com.wanderersoftherift.wotr.gui.widget.quest.GiveItemGoalWidget;
import com.wanderersoftherift.wotr.gui.widget.quest.KillMobGoalWidget;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = WanderersOfTheRift.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class WotrGoalDisplays {

    @SubscribeEvent
    private static void registerWidgets(RegisterGoalDisplaysEvent event) {
        event.register(GiveItemGoal.class, GiveItemGoalWidget::new);
        event.register(KillMobGoal.class, KillMobGoalWidget::new);
        event.register(CompleteRiftGoal.class, CompleteRiftGoalWidget::new);
        event.register(CloseAnomalyGoal.class, CloseAnomalyGoalWidget::new);
    }
}
