package com.wanderersoftherift.wotr.core.quest.goal;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.core.quest.Goal;
import com.wanderersoftherift.wotr.core.quest.QuestState;
import com.wanderersoftherift.wotr.serialization.DualCodec;
import net.minecraft.advancements.critereon.EntityTypePredicate;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;

/**
 * A goal to kill mobs
 * 
 * @param mob      A predicate for the type of mobs that must be killed
 * @param rawLabel A translation string for displaying the type of mob
 * @param count    The number of mobs that need to be killed
 */
public record KillMobGoal(EntityTypePredicate mob, String rawLabel, int count) implements Goal {

    public static final MapCodec<KillMobGoal> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    EntityTypePredicate.CODEC.fieldOf("mob").forGetter(KillMobGoal::mob),
                    Codec.STRING.fieldOf("mob_label").forGetter(KillMobGoal::rawLabel),
                    Codec.INT.optionalFieldOf("count", 1).forGetter(KillMobGoal::count)
            ).apply(instance, KillMobGoal::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, KillMobGoal> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, KillMobGoal::rawLabel, ByteBufCodecs.INT, KillMobGoal::count,
            (label, quantity) -> new KillMobGoal(null, label, quantity)
    );

    public static final DualCodec<KillMobGoal> TYPE = new DualCodec<>(CODEC, STREAM_CODEC);

    @Override
    public DualCodec<KillMobGoal> getType() {
        return TYPE;
    }

    @Override
    public void register(ServerPlayer player, QuestState questState, int goalIndex) {
        GoalEventHandler.registerKillMobGoal(player, questState, goalIndex);
    }

    /**
     * @return A component for displaying the name of the mob classification
     */
    public Component mobLabel() {
        return Component.translatable(rawLabel);
    }

}
