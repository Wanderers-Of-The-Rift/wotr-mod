package com.wanderersoftherift.wotr.core.quest.goal;

import com.wanderersoftherift.wotr.core.quest.GoalEventListenerRegistrar;
import com.wanderersoftherift.wotr.core.quest.QuestState;
import com.wanderersoftherift.wotr.core.rift.RiftEvent;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

/**
 * Event handlers for supporting goals
 */
@EventBusSubscriber
public class GoalEventHandler {

    private static final GoalEventListenerRegistrar<LivingDeathEvent> KILL_MOB_GOALS = new GoalEventListenerRegistrar<>(
            (event, player, state, index) -> {
                if (!(state.getGoal(index) instanceof KillMobGoal goal)) {
                    return;
                }
                int goalProgress = state.getGoalProgress(index);
                if (goalProgress < goal.count() && goal.mob().matches(event.getEntity().getType())) {
                    state.setGoalProgress(index, goalProgress + 1);
                }
            });

    private static final GoalEventListenerRegistrar<RiftEvent.PlayerCompletedRift> RIFT_COMPLETION_SUCCESS_GOALS = new GoalEventListenerRegistrar<>(
            (event, player, state, index) -> {
                if (!(state.getGoal(index) instanceof CompleteRiftGoal goal)) {
                    return;
                }
                int progress = state.getGoalProgress(index);
                if (progress >= goal.count()) {
                    return;
                }
                if (!event.isObjectiveComplete() && goal.completionLevel() == RiftCompletionLevel.COMPLETE) {
                    return;
                }
                if (goal.predicate().matches(event.getConfig())) {
                    state.setGoalProgress(index, progress + 1);
                }
            });

    private static final GoalEventListenerRegistrar<RiftEvent.PlayerDied> RIFT_COMPLETION_DIED_GOALS = new GoalEventListenerRegistrar<>(
            (event, player, state, index) -> {
                if (!(state.getGoal(
                        index) instanceof CompleteRiftGoal goal)) {
                    return;
                }
                int progress = state.getGoalProgress(index);
                if (progress < goal.count() && goal.completionLevel() == RiftCompletionLevel.ATTEMPT
                        && goal.predicate().matches(event.getConfig())) {
                    state.setGoalProgress(index, progress + 1);
                }
            });

    public static void registerKillMobGoal(ServerPlayer player, QuestState questState, int goalIndex) {
        KILL_MOB_GOALS.register(player, questState, goalIndex);
    }

    public static void registerRiftCompletionListener(ServerPlayer player, QuestState questState, int goalIndex) {
        RIFT_COMPLETION_SUCCESS_GOALS.register(player, questState, goalIndex);
        RIFT_COMPLETION_DIED_GOALS.register(player, questState, goalIndex);
    }

    @SubscribeEvent
    public static void onDiedInRift(RiftEvent.PlayerDied event) {
        RIFT_COMPLETION_DIED_GOALS.trigger(event.getPlayer(), event);
    }

    @SubscribeEvent
    public static void onCompletedRift(RiftEvent.PlayerCompletedRift event) {
        RIFT_COMPLETION_SUCCESS_GOALS.trigger(event.getPlayer(), event);
    }

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) {
            return;
        }
        KILL_MOB_GOALS.trigger(player, event);
    }

    @SubscribeEvent
    public static void onPlayerLeftEvent(PlayerEvent.PlayerLoggedOutEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        KILL_MOB_GOALS.unregister(player);
        RIFT_COMPLETION_SUCCESS_GOALS.unregister(player);
        RIFT_COMPLETION_DIED_GOALS.unregister(player);
    }
}
