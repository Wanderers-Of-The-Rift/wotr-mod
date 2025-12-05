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
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

/**
 * Event handlers for supporting goals
 */
@EventBusSubscriber
public class GoalEventHandler {

    @SubscribeEvent
    public static void onDiedInRift(RiftEvent.PlayerDied event) {
        // TODO: Rather than sending an event, have a registry of GoalTracking providers? Pros, cons
        NeoForge.EVENT_BUS.post(new GoalEvent.Update<>(event.getPlayer(), CompleteRiftGoal.class, (goal) -> {
            if (goal.completionLevel() == RiftCompletionLevel.ATTEMPT && goal.predicate().matches(event.getConfig())) {
                return 1;
            }
            return 0;
        }));
    }

    @SubscribeEvent
    public static void onCompletedRift(RiftEvent.PlayerCompletedRift event) {
        NeoForge.EVENT_BUS.post(new GoalEvent.Update<>(event.getPlayer(), CompleteRiftGoal.class, (goal) -> {
            if (!event.isObjectiveComplete() && goal.completionLevel() == RiftCompletionLevel.COMPLETE) {
                return 0;
            }
            if (goal.predicate().matches(event.getConfig())) {
                return 1;
            }
            return 0;
        }));
    }

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) {
            return;
        }
        NeoForge.EVENT_BUS.post(new GoalEvent.Update<>(player, KillMobGoal.class, (goal) -> {
            if (goal.mob().map(predicate -> predicate.matches(event.getEntity().getType())).orElse(true)) {
                return 1;
            }
            return 0;
        }));
    }

    @SubscribeEvent
    public static void onAnomalyClosed(AnomalyEvent.Closed event) {
        NeoForge.EVENT_BUS.post(new GoalEvent.Update<>(event.getClosingPlayer(), CloseAnomalyGoal.class, (goal) -> {
            if (goal.anomalyType().map(type -> type.value().equals(event.getTaskType())).orElse(true)) {
                return 1;
            }
            return 0;
        }));
    }

    @SubscribeEvent
    public static void onRoomFirstVisited(RiftMapEvent.RoomFirstVisited event) {
        if (event.getRoom().template().identifier().startsWith("wotr:rift/room/portal")) {
            return;
        }
        NeoForge.EVENT_BUS.post(new GoalEvent.Update<>(event.getPlayer(), VisitRoomGoal.class, (goal) -> 1));
    }

}
