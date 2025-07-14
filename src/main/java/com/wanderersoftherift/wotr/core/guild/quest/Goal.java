package com.wanderersoftherift.wotr.core.guild.quest;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.loot.LootParams;

import java.util.List;

/**
 * Interface for a goal that is part of a quest
 */
public interface Goal extends GoalProvider {
    Codec<Goal> DIRECT_CODEC = WotrRegistries.GOAL_TYPES.byNameCodec().dispatch(Goal::getType, GoalType::codec);
    StreamCodec<RegistryFriendlyByteBuf, Goal> STREAM_CODEC = ByteBufCodecs.registry(WotrRegistries.Keys.GOAL_TYPES)
            .dispatch(Goal::getType, GoalType::streamCodec);

    GoalType<?> getType();

    @Override
    default MapCodec<? extends GoalProvider> getCodec() {
        return getType().codec();
    }

    @Override
    default List<Goal> generateGoal(LootParams params) {
        return List.of(this);
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
    void register(ServerPlayer player, QuestState quest, int goalIndex);
}
