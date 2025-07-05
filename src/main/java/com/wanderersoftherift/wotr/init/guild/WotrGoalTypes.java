package com.wanderersoftherift.wotr.init.guild;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.guild.quest.Goal;
import com.wanderersoftherift.wotr.core.guild.quest.goal.GiveItemGoal;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class WotrGoalTypes {

    public static final DeferredRegister<MapCodec<? extends Goal>> GOAL_TYPES = DeferredRegister
            .create(WotrRegistries.Keys.GOAL_TYPES, WanderersOfTheRift.MODID);

    public static final Supplier<MapCodec<? extends Goal>> GIVE_ITEM_GOAL_TYPE = GOAL_TYPES.register("give_item",
            () -> GiveItemGoal.CODEC);
}
