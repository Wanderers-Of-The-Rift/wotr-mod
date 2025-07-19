package com.wanderersoftherift.wotr.core.guild.quest;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import net.minecraft.world.level.storage.loot.LootParams;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Function;

/**
 * Interface for classes that provide rewards. These rewards may be randomly generated, or otherwise differ depending on
 * the provided context.
 */
public interface RewardProvider {
    Codec<RewardProvider> DIRECT_CODEC = WotrRegistries.REWARD_PROVIDER_TYPES.byNameCodec()
            .dispatch(RewardProvider::getCodec, Function.identity());

    /**
     * @return The codec used to serialize this reward
     */
    MapCodec<? extends RewardProvider> getCodec();

    /**
     * @param params Parameters that may affect generation
     * @return A list of rewards produced to be produced by the quest
     */
    @NotNull List<Reward> generateReward(LootParams params);
}
