package com.wanderersoftherift.wotr.core.guild.quest;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import net.minecraft.world.level.storage.loot.LootParams;

import java.util.List;
import java.util.function.Function;

public interface RewardProvider {
    Codec<RewardProvider> DIRECT_CODEC = WotrRegistries.REWARD_PROVIDER_TYPES.byNameCodec()
            .dispatch(RewardProvider::getCodec, Function.identity());

    /**
     * @return The codec used to serialize this goal
     */
    MapCodec<? extends RewardProvider> getCodec();

    List<Reward> generateReward(LootParams params);
}
