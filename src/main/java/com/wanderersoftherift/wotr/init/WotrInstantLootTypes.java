package com.wanderersoftherift.wotr.init;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.loot.InstantLoot;
import com.wanderersoftherift.wotr.loot.RewardInstantLoot;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class WotrInstantLootTypes {
    public static final DeferredRegister<MapCodec<? extends InstantLoot>> INSTANT_LOOT_TYPES = DeferredRegister
            .create(WotrRegistries.INSTANT_LOOT_TYPES, WanderersOfTheRift.MODID);

    public static final DeferredHolder<MapCodec<? extends InstantLoot>, MapCodec<RewardInstantLoot>> REWARD_INSTANT_LOOT = INSTANT_LOOT_TYPES
            .register("reward", () -> RewardInstantLoot.CODEC);
}
