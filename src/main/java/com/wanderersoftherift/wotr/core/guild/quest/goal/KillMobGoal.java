package com.wanderersoftherift.wotr.core.guild.quest.goal;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.core.guild.quest.Goal;
import com.wanderersoftherift.wotr.core.guild.quest.GoalEventListener;
import com.wanderersoftherift.wotr.core.guild.quest.GoalType;
import com.wanderersoftherift.wotr.core.guild.quest.QuestEventHandler;
import com.wanderersoftherift.wotr.core.guild.quest.QuestState;
import net.minecraft.advancements.critereon.EntityTypePredicate;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;

public record KillMobGoal(EntityTypePredicate mob, String rawLabel, int quantity) implements Goal {

    public static final MapCodec<KillMobGoal> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    EntityTypePredicate.CODEC.fieldOf("mob").forGetter(KillMobGoal::mob),
                    Codec.STRING.fieldOf("mob_label").forGetter(KillMobGoal::rawLabel),
                    Codec.INT.optionalFieldOf("quantity", 1).forGetter(KillMobGoal::quantity)
            ).apply(instance, KillMobGoal::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, KillMobGoal> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, KillMobGoal::rawLabel, ByteBufCodecs.INT, KillMobGoal::quantity,
            (label, quantity) -> new KillMobGoal(null, label, quantity)
    );

    public static final GoalType<KillMobGoal> TYPE = new GoalType<>(CODEC, STREAM_CODEC);

    @Override
    public GoalType<KillMobGoal> getType() {
        return TYPE;
    }

    @Override
    public Goal generateGoal(RandomSource random) {
        return this;
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

    public EntityTypePredicate mob() {
        return mob;
    }

    public Component mobLabel() {
        return Component.translatable(rawLabel);
    }

    public int quantity() {
        return quantity;
    }

}
