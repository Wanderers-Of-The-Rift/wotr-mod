package com.wanderersoftherift.wotr.init.quest;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.goal.Goal;
import com.wanderersoftherift.wotr.core.goal.GoalProvider;
import com.wanderersoftherift.wotr.core.goal.provider.CloseAnomalyGoalProvider;
import com.wanderersoftherift.wotr.core.goal.provider.CompleteRiftGoalProvider;
import com.wanderersoftherift.wotr.core.goal.provider.FixedGoalProvider;
import com.wanderersoftherift.wotr.core.goal.provider.GiveItemGoalProvider;
import com.wanderersoftherift.wotr.core.goal.provider.KillMobGoalProvider;
import com.wanderersoftherift.wotr.core.goal.provider.PoolGoalProvider;
import com.wanderersoftherift.wotr.core.goal.type.CloseAnomalyGoal;
import com.wanderersoftherift.wotr.core.goal.type.CompleteRiftGoal;
import com.wanderersoftherift.wotr.core.goal.type.GiveItemGoal;
import com.wanderersoftherift.wotr.core.goal.type.KillMobGoal;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.serialization.DualCodec;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class WotrGoalTypes {

    public static final DeferredRegister<DualCodec<? extends Goal>> GOAL_TYPES = DeferredRegister
            .create(WotrRegistries.Keys.GOAL_TYPES, WanderersOfTheRift.MODID);
    public static final DeferredRegister<MapCodec<? extends GoalProvider>> GOAL_PROVIDER_TYPES = DeferredRegister
            .create(WotrRegistries.Keys.GOAL_PROVIDER_TYPES, WanderersOfTheRift.MODID);

    public static final Supplier<DualCodec<? extends Goal>> FIXED_GIVE_ITEM = register("fixed_give_item",
            () -> GiveItemGoal.TYPE);

    public static final Supplier<MapCodec<? extends GoalProvider>> GIVE_ITEM = GOAL_PROVIDER_TYPES.register("give_item",
            () -> GiveItemGoalProvider.CODEC);

    public static final Supplier<DualCodec<? extends Goal>> FIXED_KILL_MOB = register("fixed_kill_mob",
            () -> KillMobGoal.TYPE);

    public static final Supplier<MapCodec<? extends GoalProvider>> KILL_MOB = GOAL_PROVIDER_TYPES.register("kill_mob",
            () -> KillMobGoalProvider.CODEC);

    public static final Supplier<DualCodec<? extends Goal>> FIXED_COMPLETE_RIFT = register("fixed_complete_rift",
            () -> CompleteRiftGoal.TYPE);

    public static final Supplier<MapCodec<? extends GoalProvider>> COMPLETE_RIFT = GOAL_PROVIDER_TYPES
            .register("complete_rift", () -> CompleteRiftGoalProvider.CODEC);

    public static final Supplier<DualCodec<? extends Goal>> FIXED_CLOSE_ANOMALY = register("fixed_close_anomaly",
            () -> CloseAnomalyGoal.TYPE);

    public static final Supplier<MapCodec<? extends GoalProvider>> CLOSE_ANOMALY = GOAL_PROVIDER_TYPES
            .register("close_anomaly", () -> CloseAnomalyGoalProvider.CODEC);

    public static final Supplier<MapCodec<? extends GoalProvider>> POOL = GOAL_PROVIDER_TYPES.register("pool",
            () -> PoolGoalProvider.CODEC);

    private static Supplier<DualCodec<? extends Goal>> register(
            String id,
            Supplier<DualCodec<? extends Goal>> typeSupplier) {
        GOAL_PROVIDER_TYPES.register(id, () -> FixedGoalProvider.codec(typeSupplier.get().codec()));
        return GOAL_TYPES.register(id, typeSupplier);
    }
}
