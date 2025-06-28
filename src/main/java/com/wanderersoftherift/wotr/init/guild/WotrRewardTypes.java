package com.wanderersoftherift.wotr.init.guild;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.guild.quest.Reward;
import com.wanderersoftherift.wotr.core.guild.quest.reward.ItemReward;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class WotrRewardTypes {
    public static final DeferredRegister<MapCodec<? extends Reward>> REWARD_TYPES = DeferredRegister
            .create(WotrRegistries.Keys.REWARD_TYPES, WanderersOfTheRift.MODID);

    public static final Supplier<MapCodec<? extends Reward>> ITEM_REWARD = REWARD_TYPES.register("item",
            () -> ItemReward.CODEC);
}
