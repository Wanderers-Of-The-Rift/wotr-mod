package com.wanderersoftherift.wotr.core.goal;

import com.wanderersoftherift.wotr.block.blockentity.anomaly.AnomalyEvent;
import com.wanderersoftherift.wotr.core.goal.type.CloseAnomalyGoal;
import com.wanderersoftherift.wotr.core.goal.type.CompleteRiftGoal;
import com.wanderersoftherift.wotr.core.goal.type.KillMobGoal;
import com.wanderersoftherift.wotr.core.goal.type.RiftCompletionLevel;
import com.wanderersoftherift.wotr.core.goal.type.VisitRoomGoal;
import com.wanderersoftherift.wotr.core.rift.RiftEvent;
import com.wanderersoftherift.wotr.core.rift.map.RiftMapEvent;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

/**
 * Event handlers for supporting goals
 */
@EventBusSubscriber
public class GoalEventHandler {

    @SubscribeEvent
    public static void onDiedInRift(RiftEvent.PlayerDied event) {
        GoalManager.getGoalStates(event.getPlayer(), CompleteRiftGoal.class).forEach(goalState -> {
            CompleteRiftGoal goal = goalState.getGoal();
            if (goal.completionLevel() == RiftCompletionLevel.ATTEMPT && goal.predicate().matches(event.getConfig())) {
                goalState.incrementProgress();
            }
        });
    }

    @SubscribeEvent
    public static void onCompletedRift(RiftEvent.PlayerCompletedRift event) {
        GoalManager.getGoalStates(event.getPlayer(), CompleteRiftGoal.class).forEach(state -> {
            CompleteRiftGoal goal = state.getGoal();
            if (!event.isObjectiveComplete() && goal.completionLevel() == RiftCompletionLevel.COMPLETE) {
                return;
            }
            if (goal.predicate().matches(event.getConfig())) {
                state.incrementProgress();
            }
        });
    }

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) {
            return;
        }
        GoalManager.getGoalStates(player, KillMobGoal.class).forEach(state -> {
            if (state.getGoal().mob().map(predicate -> predicate.matches(event.getEntity().getType())).orElse(true)) {
                state.incrementProgress();
            }
        });
    }

    @SubscribeEvent
    public static void onAnomalyClosed(AnomalyEvent.Closed event) {
        GoalManager.getGoalStates(event.getClosingPlayer(), CloseAnomalyGoal.class).forEach(state -> {
            if (state.getGoal().anomalyType().map(type -> type.value().equals(event.getTaskType())).orElse(true)) {
                state.incrementProgress();
            }
        });
    }

    @SubscribeEvent
    public static void onRoomFirstVisited(RiftMapEvent.RoomFirstVisited event) {
        if (event.getRoom().template().identifier().path().startsWith("rift/room/portal")) {
            return;
        }
        GoalManager.getGoalStates(event.getPlayer(), VisitRoomGoal.class).forEach(GoalState::incrementProgress);
    }

}
