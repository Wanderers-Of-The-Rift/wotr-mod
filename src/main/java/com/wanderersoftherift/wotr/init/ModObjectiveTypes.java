package com.wanderersoftherift.wotr.init;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.rift.objective.ObjectiveType;
import com.wanderersoftherift.wotr.rift.objective.definition.KillObjective;
import com.wanderersoftherift.wotr.rift.objective.definition.StealthObjective;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;

import java.util.function.Supplier;

public class ModObjectiveTypes {
    public static final ResourceKey<Registry<MapCodec<? extends ObjectiveType>>> OBJECTIVE_TYPE_KEY = ResourceKey
            .createRegistryKey(WanderersOfTheRift.id("objective_type"));

    public static final Registry<MapCodec<? extends ObjectiveType>> OBJECTIVE_TYPE_REGISTRY = new RegistryBuilder<>(
            OBJECTIVE_TYPE_KEY).create();

    // Creating the Deferred Register
    public static final DeferredRegister<MapCodec<? extends ObjectiveType>> OBJECTIVE_TYPES = DeferredRegister
            .create(OBJECTIVE_TYPE_KEY, WanderersOfTheRift.MODID);

    public static final Supplier<MapCodec<? extends ObjectiveType>> STEALTH = OBJECTIVE_TYPES.register("stealth",
            () -> StealthObjective.CODEC);

    public static final Supplier<MapCodec<? extends ObjectiveType>> KILL = OBJECTIVE_TYPES.register("kill",
            () -> KillObjective.CODEC);

}
