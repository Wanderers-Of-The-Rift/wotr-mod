package com.wanderersoftherift.wotr.init.guild;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.guild.quest.RewardProvider;
import com.wanderersoftherift.wotr.core.guild.quest.RewardType;
import com.wanderersoftherift.wotr.core.guild.quest.reward.ItemReward;
import com.wanderersoftherift.wotr.core.guild.quest.reward.provider.LootTableRewardProvider;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class WotrRewardTypes {
    public static final DeferredRegister<MapCodec<? extends RewardProvider>> REWARD_PROVIDER_TYPES = DeferredRegister
            .create(WotrRegistries.Keys.REWARD_PROVIDER_TYPES, WanderersOfTheRift.MODID);
    public static final DeferredRegister<RewardType<?>> REWARD_TYPES = DeferredRegister
            .create(WotrRegistries.Keys.REWARD_TYPES, WanderersOfTheRift.MODID);

    public static final Supplier<MapCodec<? extends RewardProvider>> LOOT_TABLE_RWARD = REWARD_PROVIDER_TYPES
            .register("loot_table", () -> LootTableRewardProvider.CODEC);
    public static final Supplier<RewardType<?>> ITEM_REWARD = register("item", () -> ItemReward.TYPE);

    private WotrRewardTypes() {
    }

    private static Supplier<RewardType<?>> register(String id, Supplier<RewardType<?>> supplier) {
        REWARD_PROVIDER_TYPES.register(id, () -> supplier.get().codec());
        return REWARD_TYPES.register(id, supplier);
    }
}
