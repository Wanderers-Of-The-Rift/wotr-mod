package com.wanderersoftherift.wotr.init.guild;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.guild.quest.GoalProvider;
import com.wanderersoftherift.wotr.core.guild.quest.GoalType;
import com.wanderersoftherift.wotr.core.guild.quest.goal.CompleteRiftGoal;
import com.wanderersoftherift.wotr.core.guild.quest.goal.GiveItemGoal;
import com.wanderersoftherift.wotr.core.guild.quest.goal.KillMobGoal;
import com.wanderersoftherift.wotr.core.guild.quest.goal.provider.CompleteRiftGoalProvider;
import com.wanderersoftherift.wotr.core.guild.quest.goal.provider.GiveItemGoalProvider;
import com.wanderersoftherift.wotr.core.guild.quest.goal.provider.PoolGoalProvider;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class WotrGoalTypes {

    public static final DeferredRegister<GoalType<?>> GOAL_TYPES = DeferredRegister
            .create(WotrRegistries.Keys.GOAL_TYPES, WanderersOfTheRift.MODID);
    public static final DeferredRegister<MapCodec<? extends GoalProvider>> GOAL_PROVIDER_TYPES = DeferredRegister
            .create(WotrRegistries.Keys.GOAL_PROVIDER_TYPES, WanderersOfTheRift.MODID);

    public static final Supplier<GoalType<?>> FIXED_GIVE_ITEM = register("fixed_give_item", () -> GiveItemGoal.TYPE);

    public static final Supplier<GoalType<?>> FIXED_KILL_MOB = register("fixed_kill_mob", () -> KillMobGoal.TYPE);

    public static final Supplier<GoalType<?>> FIXED_COMPLETE_RIFT = register("fixed_complete_rift",
            () -> CompleteRiftGoal.TYPE);

    public static final Supplier<MapCodec<? extends GoalProvider>> GIVE_ITEM = GOAL_PROVIDER_TYPES.register("give_item",
            () -> GiveItemGoalProvider.CODEC);

    public static final Supplier<MapCodec<? extends GoalProvider>> COMPLETE_RIFT = GOAL_PROVIDER_TYPES
            .register("complete_rift", () -> CompleteRiftGoalProvider.CODEC);

    public static final Supplier<MapCodec<? extends GoalProvider>> POOL = GOAL_PROVIDER_TYPES.register("pool",
            () -> PoolGoalProvider.CODEC);

    private static Supplier<GoalType<?>> register(String id, Supplier<GoalType<?>> typeSupplier) {
        GOAL_PROVIDER_TYPES.register(id, () -> typeSupplier.get().codec());
        return GOAL_TYPES.register(id, typeSupplier);
    }
}
