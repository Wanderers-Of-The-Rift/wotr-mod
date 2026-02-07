package com.wanderersoftherift.wotr.init.client;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.goal.type.ActivateObjectiveGoal;
import com.wanderersoftherift.wotr.core.goal.type.CloseAnomalyGoal;
import com.wanderersoftherift.wotr.core.goal.type.CollectItemGoal;
import com.wanderersoftherift.wotr.core.goal.type.CompleteRiftGoal;
import com.wanderersoftherift.wotr.core.goal.type.GiveItemGoal;
import com.wanderersoftherift.wotr.core.goal.type.KillMobGoal;
import com.wanderersoftherift.wotr.core.goal.type.TossItemGoal;
import com.wanderersoftherift.wotr.core.goal.type.VisitRoomGoal;
import com.wanderersoftherift.wotr.gui.widget.goal.ActivateObjectiveGoalWidget;
import com.wanderersoftherift.wotr.gui.widget.goal.CloseAnomalyGoalWidget;
import com.wanderersoftherift.wotr.gui.widget.goal.CompleteRiftGoalWidget;
import com.wanderersoftherift.wotr.gui.widget.goal.ItemGoalWidget;
import com.wanderersoftherift.wotr.gui.widget.goal.KillMobGoalWidget;
import com.wanderersoftherift.wotr.gui.widget.goal.VisitRoomGoalWidget;
import com.wanderersoftherift.wotr.gui.widget.lookup.RegisterGoalDisplaysEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = WanderersOfTheRift.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class WotrGoalDisplays {

    @SubscribeEvent
    private static void registerWidgets(RegisterGoalDisplaysEvent event) {
        event.register(GiveItemGoal.class,
                goal -> new ItemGoalWidget(goal, WanderersOfTheRift.translationId("container", "quest.goal.give")));
        event.register(CollectItemGoal.class,
                goal -> new ItemGoalWidget(goal, WanderersOfTheRift.translationId("container", "quest.goal.collect")));
        event.register(TossItemGoal.class,
                goal -> new ItemGoalWidget(goal, WanderersOfTheRift.translationId("container", "quest.goal.toss")));
        event.register(KillMobGoal.class, KillMobGoalWidget::new);
        event.register(CompleteRiftGoal.class, CompleteRiftGoalWidget::new);
        event.register(CloseAnomalyGoal.class, CloseAnomalyGoalWidget::new);
        event.register(VisitRoomGoal.class, VisitRoomGoalWidget::new);
        event.register(ActivateObjectiveGoal.class, ActivateObjectiveGoalWidget::new);
    }
}
