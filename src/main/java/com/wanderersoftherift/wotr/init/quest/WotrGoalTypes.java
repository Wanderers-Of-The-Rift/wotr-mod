package com.wanderersoftherift.wotr.init.quest;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.goal.Goal;
import com.wanderersoftherift.wotr.core.goal.GoalProvider;
import com.wanderersoftherift.wotr.core.goal.provider.ActivateObjectiveGoalProvider;
import com.wanderersoftherift.wotr.core.goal.provider.CloseAnomalyGoalProvider;
import com.wanderersoftherift.wotr.core.goal.provider.CollectFromLoottableGoalProvider;
import com.wanderersoftherift.wotr.core.goal.provider.CollectItemGoalProvider;
import com.wanderersoftherift.wotr.core.goal.provider.CompleteRiftGoalProvider;
import com.wanderersoftherift.wotr.core.goal.provider.GiveItemGoalProvider;
import com.wanderersoftherift.wotr.core.goal.provider.KillMobGoalProvider;
import com.wanderersoftherift.wotr.core.goal.provider.PoolGoalProvider;
import com.wanderersoftherift.wotr.core.goal.provider.VisitRoomGoalProvider;
import com.wanderersoftherift.wotr.core.goal.type.ActivateObjectiveGoal;
import com.wanderersoftherift.wotr.core.goal.type.CloseAnomalyGoal;
import com.wanderersoftherift.wotr.core.goal.type.CollectItemGoal;
import com.wanderersoftherift.wotr.core.goal.type.CompleteRiftGoal;
import com.wanderersoftherift.wotr.core.goal.type.GiveItemGoal;
import com.wanderersoftherift.wotr.core.goal.type.KillMobGoal;
import com.wanderersoftherift.wotr.core.goal.type.VisitRoomGoal;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.serialization.DualCodec;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class WotrGoalTypes {

    public static final DeferredRegister<DualCodec<? extends Goal>> GOAL_TYPES = DeferredRegister
            .create(WotrRegistries.Keys.GOAL_TYPES, WanderersOfTheRift.MODID);

    public static final DeferredRegister<MapCodec<? extends GoalProvider>> GOAL_PROVIDER_TYPES = DeferredRegister
            .create(WotrRegistries.Keys.GOAL_PROVIDER_TYPES, WanderersOfTheRift.MODID);

    public static final Supplier<DualCodec<? extends Goal>> GIVE_ITEM = GOAL_TYPES.register("give_item",
            () -> GiveItemGoal.TYPE);

    public static final Supplier<MapCodec<? extends GoalProvider>> GIVE_ITEM_PROVIDER = GOAL_PROVIDER_TYPES
            .register("give_item", () -> GiveItemGoalProvider.CODEC);

    public static final Supplier<DualCodec<? extends Goal>> KILL_MOB = GOAL_TYPES.register("kill_mob",
            () -> KillMobGoal.TYPE);

    public static final Supplier<MapCodec<? extends GoalProvider>> KILL_MOB_PROVIDER = GOAL_PROVIDER_TYPES
            .register("kill_mob", () -> KillMobGoalProvider.CODEC);

    public static final Supplier<DualCodec<? extends Goal>> COMPLETE_RIFT = GOAL_TYPES.register("complete_rift",
            () -> CompleteRiftGoal.TYPE);

    public static final Supplier<MapCodec<? extends GoalProvider>> COMPLETE_RIFT_PROVIDER = GOAL_PROVIDER_TYPES
            .register("complete_rift", () -> CompleteRiftGoalProvider.CODEC);

    public static final Supplier<DualCodec<? extends Goal>> CLOSE_ANOMALY = GOAL_TYPES.register("close_anomaly",
            () -> CloseAnomalyGoal.TYPE);

    public static final Supplier<MapCodec<? extends GoalProvider>> CLOSE_ANOMALY_PROVIDER = GOAL_PROVIDER_TYPES
            .register("close_anomaly", () -> CloseAnomalyGoalProvider.CODEC);

    public static final Supplier<DualCodec<? extends Goal>> VISIT_ROOM = GOAL_TYPES.register("visit_room",
            () -> VisitRoomGoal.TYPE);

    public static final Supplier<MapCodec<? extends GoalProvider>> VISIT_ROOM_PROVIDER = GOAL_PROVIDER_TYPES
            .register("visit_room", () -> VisitRoomGoalProvider.CODEC);

    public static final Supplier<DualCodec<? extends Goal>> ACTIVATE_OBJECTIVE = GOAL_TYPES.register(
            "activate_objective_block", () -> ActivateObjectiveGoal.TYPE);

    public static final Supplier<MapCodec<? extends GoalProvider>> ACTIVATE_OBJECTIVE_PROVIDER = GOAL_PROVIDER_TYPES
            .register("activate_objective_block", () -> ActivateObjectiveGoalProvider.CODEC);

    public static final Supplier<DualCodec<? extends Goal>> COLLECT_ITEM = GOAL_TYPES.register("collect_item",
            () -> CollectItemGoal.TYPE);

    public static final Supplier<MapCodec<? extends GoalProvider>> COLLECT_ITEM_PROVIDER = GOAL_PROVIDER_TYPES
            .register("collect_item", () -> CollectItemGoalProvider.CODEC);

    public static final Supplier<MapCodec<? extends GoalProvider>> COLLECT_FROM_LOOT_TABLE_PROVIDER = GOAL_PROVIDER_TYPES
            .register("collect_from_loot_table", () -> CollectFromLoottableGoalProvider.CODEC);

    public static final Supplier<MapCodec<? extends GoalProvider>> POOL = GOAL_PROVIDER_TYPES.register("pool",
            () -> PoolGoalProvider.CODEC);

}
