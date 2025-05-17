package com.wanderersoftherift.wotr.init;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.rift.objective.OngoingObjective;
import com.wanderersoftherift.wotr.rift.objective.ongoing.KillOngoingObjective;
import com.wanderersoftherift.wotr.rift.objective.ongoing.StealthOngoingObjective;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class WotrOngoingObjectiveTypes {

    public static final DeferredRegister<MapCodec<? extends OngoingObjective>> ONGOING_OBJECTIVE_TYPES = DeferredRegister
            .create(WotrRegistries.Keys.ONGOING_OBJECTIVE_TYPES, WanderersOfTheRift.MODID);

    public static final Supplier<MapCodec<? extends OngoingObjective>> STEALTH = ONGOING_OBJECTIVE_TYPES
            .register("stealth", () -> StealthOngoingObjective.CODEC);

    public static final Supplier<MapCodec<? extends OngoingObjective>> KILL = ONGOING_OBJECTIVE_TYPES.register("kill",
            () -> KillOngoingObjective.CODEC);
}
