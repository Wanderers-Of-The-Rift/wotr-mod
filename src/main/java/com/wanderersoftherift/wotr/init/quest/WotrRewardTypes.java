package com.wanderersoftherift.wotr.init.quest;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.quest.Reward;
import com.wanderersoftherift.wotr.core.quest.RewardProvider;
import com.wanderersoftherift.wotr.core.quest.reward.CurrencyReward;
import com.wanderersoftherift.wotr.core.quest.reward.ItemReward;
import com.wanderersoftherift.wotr.core.quest.reward.ReputationReward;
import com.wanderersoftherift.wotr.core.quest.reward.provider.CurrencyRewardProvider;
import com.wanderersoftherift.wotr.core.quest.reward.provider.FixedRewardProvider;
import com.wanderersoftherift.wotr.core.quest.reward.provider.LootTableRewardProvider;
import com.wanderersoftherift.wotr.core.quest.reward.provider.ReputationRewardProvider;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.serialization.DualCodec;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class WotrRewardTypes {
    public static final DeferredRegister<MapCodec<? extends RewardProvider>> REWARD_PROVIDER_TYPES = DeferredRegister
            .create(WotrRegistries.Keys.REWARD_PROVIDER_TYPES, WanderersOfTheRift.MODID);
    public static final DeferredRegister<DualCodec<? extends Reward>> REWARD_TYPES = DeferredRegister
            .create(WotrRegistries.Keys.REWARD_TYPES, WanderersOfTheRift.MODID);

    public static final Supplier<MapCodec<? extends RewardProvider>> LOOT_TABLE_REWARD = REWARD_PROVIDER_TYPES
            .register("loot_table", () -> LootTableRewardProvider.CODEC);
    public static final Supplier<MapCodec<? extends RewardProvider>> CURRENCY_REWARD_PROVIDER = REWARD_PROVIDER_TYPES
            .register("currency", () -> CurrencyRewardProvider.CODEC);
    public static final Supplier<MapCodec<? extends RewardProvider>> REPUTATION_REWARD_PROVIDER = REWARD_PROVIDER_TYPES
            .register("reputation", () -> ReputationRewardProvider.CODEC);
    public static final Supplier<DualCodec<? extends Reward>> ITEM_REWARD = registerWithFixedProvider("item",
            () -> ItemReward.TYPE);
    public static final Supplier<DualCodec<? extends Reward>> CURRENCY_REWARD = REWARD_TYPES.register("currency",
            () -> CurrencyReward.TYPE);
    public static final Supplier<DualCodec<? extends Reward>> REPUTATION_REWARD = REWARD_TYPES.register("reputation",
            () -> ReputationReward.TYPE);

    private WotrRewardTypes() {
    }

    private static Supplier<DualCodec<? extends Reward>> registerWithFixedProvider(
            String id,
            Supplier<DualCodec<? extends Reward>> supplier) {
        REWARD_PROVIDER_TYPES.register(id, () -> FixedRewardProvider.codec(supplier.get().codec()));
        return REWARD_TYPES.register(id, supplier);
    }
}
