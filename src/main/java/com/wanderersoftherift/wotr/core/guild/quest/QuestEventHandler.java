package com.wanderersoftherift.wotr.core.guild.quest;

import com.wanderersoftherift.wotr.core.guild.quest.goal.CompleteRiftGoal;
import com.wanderersoftherift.wotr.core.guild.quest.goal.KillMobGoal;
import com.wanderersoftherift.wotr.core.guild.quest.goal.RiftCompletionLevel;
import com.wanderersoftherift.wotr.core.guild.quest.goal.RiftPredicate;
import com.wanderersoftherift.wotr.core.rift.RiftEvent;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber
public class QuestEventHandler {

    private static final GoalEventListenerRegistrar<LivingDeathEvent> KILL_MOB_GOALS = new GoalEventListenerRegistrar<>(
            (event, player, state, index) -> {
                if (!(state.getGoal(index) instanceof KillMobGoal goal)) {
                    return;
                }
                int goalProgress = state.getGoalProgress(index);
                if (goalProgress < goal.progressTarget() && goal.mob().matches(event.getEntity().getType())) {
                    state.setGoalProgress(index, goalProgress + 1);
                }
            });

    private static final GoalEventListenerRegistrar<RiftEvent.PlayerCompletedRift> RIFT_COMPLETION_SUCCESS_GOALS = new GoalEventListenerRegistrar<>(
            (event, player, state, index) -> {
                if (!(state.getGoal(
                        index) instanceof CompleteRiftGoal(int count, RiftCompletionLevel completionLevel, RiftPredicate predicate))) {
                    return;
                }
                int progress = state.getGoalProgress(index);
                if (progress >= count) {
                    return;
                }
                if (!event.isObjectiveComplete() && completionLevel == RiftCompletionLevel.COMPLETE) {
                    return;
                }
                if (predicate.matches(event.getConfig())) {
                    state.setGoalProgress(index, progress + 1);
                }
            });

    private static final GoalEventListenerRegistrar<RiftEvent.PlayerDied> RIFT_COMPLETION_DIED_GOALS = new GoalEventListenerRegistrar<>(
            (event, player, state, index) -> {
                if (!(state.getGoal(
                        index) instanceof CompleteRiftGoal(int count, RiftCompletionLevel completionLevel, RiftPredicate predicate))) {
                    return;
                }
                int progress = state.getGoalProgress(index);
                if (progress < count && completionLevel == RiftCompletionLevel.ENTER
                        && predicate.matches(event.getConfig())) {
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
