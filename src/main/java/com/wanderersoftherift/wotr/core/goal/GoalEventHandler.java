package com.wanderersoftherift.wotr.core.goal;

import com.wanderersoftherift.wotr.core.goal.type.CompleteRiftGoal;
import com.wanderersoftherift.wotr.core.goal.type.KillMobGoal;
import com.wanderersoftherift.wotr.core.goal.type.RiftCompletionLevel;
import com.wanderersoftherift.wotr.core.quest.ActiveQuests;
import com.wanderersoftherift.wotr.core.rift.RiftData;
import com.wanderersoftherift.wotr.core.rift.RiftEvent;
import com.wanderersoftherift.wotr.core.rift.RiftLevelManager;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

import java.util.function.Function;

/**
 * Event handlers for supporting goals
 */
@EventBusSubscriber
public class GoalEventHandler {

    private static <T extends Goal> void updateGoal(
            Player player,
            Class<T> goalType,
            Function<T, Integer> progressFunction) {
        ActiveQuests activeQuests = player.getData(WotrAttachments.ACTIVE_QUESTS.get());
        if (!(player.level() instanceof ServerLevel level)) {
            return;
        }
        activeQuests.progressGoals(goalType, progressFunction, level);
        if (RiftLevelManager.isRift(level)) {
            RiftData data = RiftData.get(level);
            data.progressGoals(goalType, progressFunction, level);
        }
    }

    @SubscribeEvent
    public static void onDiedInRift(RiftEvent.PlayerDied event) {
        updateGoal(event.getPlayer(), CompleteRiftGoal.class, (goal) -> {
            if (goal.completionLevel() == RiftCompletionLevel.ATTEMPT && goal.predicate().matches(event.getConfig())) {
                return 1;
            }
            return 0;
        });
    }

    @SubscribeEvent
    public static void onCompletedRift(RiftEvent.PlayerCompletedRift event) {
        updateGoal(event.getPlayer(), CompleteRiftGoal.class, (goal) -> {
            if (!event.isObjectiveComplete() && goal.completionLevel() == RiftCompletionLevel.COMPLETE) {
                return 0;
            }
            if (goal.predicate().matches(event.getConfig())) {
                return 1;
            }
            return 0;
        });
    }

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) {
            return;
        }
        updateGoal(player, KillMobGoal.class, (goal) -> {
            if (goal.mob().map(predicate -> predicate.matches(event.getEntity().getType())).orElse(true)) {
                return 1;
            }
            return 0;
        });
    }

}
