package com.wanderersoftherift.wotr.core.guild.quest.goal;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.core.guild.quest.Goal;
import com.wanderersoftherift.wotr.core.guild.quest.GoalEventListener;
import com.wanderersoftherift.wotr.core.guild.quest.QuestEventHandler;
import com.wanderersoftherift.wotr.core.guild.quest.QuestState;
import net.minecraft.advancements.critereon.EntityTypePredicate;
import net.minecraft.server.level.ServerPlayer;

public record KillMobGoal(EntityTypePredicate mob, int quantity) implements Goal {

    public static final MapCodec<KillMobGoal> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    EntityTypePredicate.CODEC.fieldOf("mob").forGetter(KillMobGoal::mob),
                    Codec.INT.optionalFieldOf("quantity", 1).forGetter(KillMobGoal::quantity)
            ).apply(instance, KillMobGoal::new));

    @Override
    public MapCodec<? extends Goal> getCodec() {
        return CODEC;
    }

    @Override
    public int progressTarget() {
        return quantity;
    }

    @Override
    public void registerActiveQuest(ServerPlayer player, QuestState questState, int goalIndex) {
        QuestEventHandler.registerPlayerKillListener(player,
                new GoalEventListener<>(questState, goalIndex, (event, state, index) -> {
                    if (mob.matches(event.getEntity().getType())) {
                        int goalProgress = state.getGoalProgress(index);
                        if (goalProgress < progressTarget()) {
                            state.setGoalProgress(index, goalProgress + 1);
                        }
                    }
                }));
    }

}
