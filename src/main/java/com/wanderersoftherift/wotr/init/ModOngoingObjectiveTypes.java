package com.wanderersoftherift.wotr.init;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.rift.objective.OngoingObjective;
import com.wanderersoftherift.wotr.rift.objective.ongoing.KillOngoingObjective;
import com.wanderersoftherift.wotr.rift.objective.ongoing.StealthOngoingObjective;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;

import java.util.function.Supplier;

public class ModOngoingObjectiveTypes {
    public static final ResourceKey<Registry<MapCodec<? extends OngoingObjective>>> ONGOING_OBJECTIVE_TYPE_KEY = ResourceKey
            .createRegistryKey(WanderersOfTheRift.id("ongoing_objective_type"));

    public static final Registry<MapCodec<? extends OngoingObjective>> ONGOING_OBJECTIVE_TYPE_REGISTRY = new RegistryBuilder<>(
            ONGOING_OBJECTIVE_TYPE_KEY).create();

    public static final DeferredRegister<MapCodec<? extends OngoingObjective>> ONGOING_OBJECTIVE_TYPES = DeferredRegister
            .create(ONGOING_OBJECTIVE_TYPE_KEY, WanderersOfTheRift.MODID);

    public static final Supplier<MapCodec<? extends OngoingObjective>> STEALTH = ONGOING_OBJECTIVE_TYPES
            .register("stealth", () -> StealthOngoingObjective.CODEC);

    public static final Supplier<MapCodec<? extends OngoingObjective>> KILL = ONGOING_OBJECTIVE_TYPES.register("kill",
            () -> KillOngoingObjective.CODEC);
}
