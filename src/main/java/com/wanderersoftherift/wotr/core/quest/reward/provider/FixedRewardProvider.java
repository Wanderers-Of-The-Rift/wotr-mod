package com.wanderersoftherift.wotr.core.quest.reward.provider;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.core.quest.Reward;
import com.wanderersoftherift.wotr.core.quest.RewardProvider;
import net.minecraft.world.level.storage.loot.LootParams;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * A provider that wraps a single reward
 * 
 * @param reward
 * @param <T>
 */
public record FixedRewardProvider<T extends Reward>(T reward) implements RewardProvider {

    public static <T extends Reward> MapCodec<FixedRewardProvider<T>> codec(MapCodec<T> rewardCodec) {
        return rewardCodec.xmap(FixedRewardProvider::new, FixedRewardProvider::reward);
    }

    @Override
    public MapCodec<? extends RewardProvider> getCodec() {
        return codec(reward.getType().codec());
    }

    @Override
    public @NotNull List<Reward> generateReward(LootParams params) {
        return List.of(reward);
    }

}