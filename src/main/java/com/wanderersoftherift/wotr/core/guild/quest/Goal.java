package com.wanderersoftherift.wotr.core.guild.quest;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.loot.LootContext;

/**
 * Interface for a goal that is part of a quest
 */
public interface Goal extends GoalDefinition {
    Codec<Goal> DIRECT_CODEC = WotrRegistries.GOAL_TYPES.byNameCodec().dispatch(Goal::getType, GoalType::codec);
    StreamCodec<RegistryFriendlyByteBuf, Goal> STREAM_CODEC = ByteBufCodecs.registry(WotrRegistries.Keys.GOAL_TYPES)
            .dispatch(Goal::getType, GoalType::streamCodec);

    GoalType<?> getType();

    default MapCodec<? extends GoalDefinition> getCodec() {
        return getType().codec();
    }

    default Goal generateGoal(LootContext context) {
        return this;
    }

    /**
     * @return The target progress count for this goal (e.g. quantity of items to hand in, count of mobs to kill)
     */
    int progressTarget();

    /**
     * Used to register any event handler for this goal
     *
     * @param player    The player this goal belongs to
     * @param quest     The quest this goal belongs to
     * @param goalIndex The index of this goal in its quest
     */
    void registerActiveQuest(ServerPlayer player, QuestState quest, int goalIndex);
}
