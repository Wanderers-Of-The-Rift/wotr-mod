package com.wanderersoftherift.wotr.init;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.rift.objective.ObjectiveType;
import com.wanderersoftherift.wotr.core.rift.objective.definition.GoalBasedObjective;
import com.wanderersoftherift.wotr.core.rift.objective.definition.NoObjective;
import com.wanderersoftherift.wotr.core.rift.objective.definition.StealthObjective;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class WotrObjectiveTypes {

    public static final DeferredRegister<MapCodec<? extends ObjectiveType>> OBJECTIVE_TYPES = DeferredRegister
            .create(WotrRegistries.Keys.OBJECTIVE_TYPES, WanderersOfTheRift.MODID);

    public static final Supplier<MapCodec<? extends ObjectiveType>> GOAL_BASED = OBJECTIVE_TYPES.register("goal_based",
            () -> GoalBasedObjective.CODEC);

    public static final Supplier<MapCodec<? extends ObjectiveType>> STEALTH = OBJECTIVE_TYPES.register("stealth",
            () -> StealthObjective.CODEC);

    public static final Supplier<MapCodec<? extends NoObjective>> NOTHING = OBJECTIVE_TYPES.register("none",
            () -> NoObjective.CODEC);

}
