package com.wanderersoftherift.wotr.core.guild.quest;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.loot.LootParams;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Interface for a goal that is part of a quest
 * <p>
 * All goals are goal providers that provide themselves - this allows quests to have specific goals in addition to goals
 * with random elements
 * </p>
 */
public interface Goal extends GoalProvider {
    Codec<Goal> DIRECT_CODEC = WotrRegistries.GOAL_TYPES.byNameCodec().dispatch(Goal::getType, GoalType::codec);
    StreamCodec<RegistryFriendlyByteBuf, Goal> STREAM_CODEC = ByteBufCodecs.registry(WotrRegistries.Keys.GOAL_TYPES)
            .dispatch(Goal::getType, GoalType::streamCodec);

    GoalType<?> getType();

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

    /// Overrides to allow Goals to act as GoalProviders

    @Override
    default MapCodec<? extends GoalProvider> getCodec() {
        return getType().codec();
    }

    @Override
    default @NotNull List<Goal> generateGoal(LootParams params) {
        return List.of(this);
    }
}
