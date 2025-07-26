package com.wanderersoftherift.wotr.core.quest;

import com.mojang.serialization.Codec;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.serialization.DualCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;

/**
 * Interface for a goal that is part of a quest
 */
public interface Goal {
    Codec<Goal> DIRECT_CODEC = WotrRegistries.GOAL_TYPES.byNameCodec().dispatch(Goal::getType, DualCodec::codec);
    StreamCodec<RegistryFriendlyByteBuf, Goal> STREAM_CODEC = ByteBufCodecs.registry(WotrRegistries.Keys.GOAL_TYPES)
            .dispatch(Goal::getType, DualCodec::streamCodec);

    DualCodec<? extends Goal> getType();

    /**
     * @return The count of progress required to complete this goal (e.g. quantity of items to hand in, count of mobs to
     *         kill, or just has the goal been met for singular goals)
     */
    int count();

    /**
     * Used to register an event handler for this goal, if needed
     *
     * @param player    The player with an instance of this goal
     * @param quest     The quest with an instance of this goal
     * @param goalIndex The index of this goal in the quest
     */
    void register(ServerPlayer player, QuestState quest, int goalIndex);
}
