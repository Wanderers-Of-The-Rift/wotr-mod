package com.wanderersoftherift.wotr.core.quest.goal;

import com.wanderersoftherift.wotr.core.quest.ActiveQuests;
import com.wanderersoftherift.wotr.core.rift.RiftEvent;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import net.minecraft.server.level.ServerPlayer;
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
        ActiveQuests activeQuests = event.getPlayer().getData(WotrAttachments.ACTIVE_QUESTS.get());
        activeQuests.progressGoals(CompleteRiftGoal.class, (goal) -> {
            if (goal.completionLevel() == RiftCompletionLevel.ATTEMPT && goal.predicate().matches(event.getConfig())) {
                return 1;
            }
            return 0;
        });
    }

    @SubscribeEvent
    public static void onCompletedRift(RiftEvent.PlayerCompletedRift event) {
        ActiveQuests activeQuests = event.getPlayer().getData(WotrAttachments.ACTIVE_QUESTS.get());
        activeQuests.progressGoals(CompleteRiftGoal.class, (goal) -> {
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
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) {
            return;
        }
        ActiveQuests activeQuests = player.getData(WotrAttachments.ACTIVE_QUESTS.get());
        activeQuests.progressGoals(KillMobGoal.class, (goal) -> {
            if (goal.mob().matches(event.getEntity().getType())) {
                return 1;
            }
            return 0;
        });
    }

}
