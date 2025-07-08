package com.wanderersoftherift.wotr.core.guild.quest;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.codec.LaxRegistryCodec;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.Function;

/**
 * Interface for a goal that is part of a quest
 */
public interface Goal {
    Codec<Goal> DIRECT_CODEC = WotrRegistries.GOAL_TYPES.byNameCodec().dispatch(Goal::getCodec, Function.identity());
    Codec<Holder<Goal>> CODEC = LaxRegistryCodec.create(WotrRegistries.Keys.GOALS);
    StreamCodec<RegistryFriendlyByteBuf, Holder<Goal>> STREAM_CODEC = ByteBufCodecs
            .holderRegistry(WotrRegistries.Keys.GOALS);

    /**
     * @return The codec used to serialize this goal
     */
    MapCodec<? extends Goal> getCodec();

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
