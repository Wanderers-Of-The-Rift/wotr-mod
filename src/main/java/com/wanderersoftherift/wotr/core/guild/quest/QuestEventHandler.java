package com.wanderersoftherift.wotr.core.guild.quest;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.wanderersoftherift.wotr.core.guild.quest.goal.KillMobGoal;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.WeakHashMap;

@EventBusSubscriber
public class QuestEventHandler {

    private static final Multimap<ServerPlayer, KillMobEntry> killMobGoals = Multimaps
            .newListMultimap(new WeakHashMap<>(), ArrayList::new);

    private static void registerGoals(ServerPlayer player) {
        for (QuestState questState : player.getData(WotrAttachments.ACTIVE_QUESTS).getQuestList()) {
            for (int goalIndex = 0; goalIndex < questState.goalCount(); goalIndex++) {
                questState.getGoal(goalIndex).registerActiveQuest(player, questState, goalIndex);
            }
        }
    }

    public static void registerKillMobGoal(ServerPlayer player, KillMobGoal goal, QuestState quest, int goalIndex) {
        killMobGoals.put(player, new KillMobEntry(goal, new WeakReference<>(quest), goalIndex));
    }

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) {
            return;
        }

        Iterator<KillMobEntry> iterator = killMobGoals.get(player).iterator();
        while (iterator.hasNext()) {
            KillMobEntry entry = iterator.next();
            QuestState questState = entry.quest().get();
            if (questState != null) {
                int progress = questState.getGoalProgress(entry.goalIndex);
                if (progress < entry.goal.progressTarget() && entry.goal.mob().matches(event.getEntity().getType())) {
                    questState.setGoalProgress(entry.goalIndex, progress + 1);
                }
            } else {
                iterator.remove();
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLeftEvent(PlayerEvent.PlayerLoggedOutEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        killMobGoals.removeAll(player);
    }

    private record KillMobEntry(KillMobGoal goal, WeakReference<QuestState> quest, int goalIndex) {
    }

}
