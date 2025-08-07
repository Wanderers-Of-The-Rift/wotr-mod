package com.wanderersoftherift.wotr.init.worldgen;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.world.level.levelgen.space.SerializableCorridorValidator;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class WotrCorridorValidators {
    public static final DeferredRegister<MapCodec<? extends SerializableCorridorValidator>> CORRIDOR_VALIDATORS = DeferredRegister
            .create(WotrRegistries.RIFT_CORRIDOR_VALIDATORS, WanderersOfTheRift.MODID);

    public static final Supplier<MapCodec<SerializableCorridorValidator.Identity>> IDENTITY_CORRIDOR_VALIDATOR = CORRIDOR_VALIDATORS
            .register("identity", () -> SerializableCorridorValidator.Identity.CODEC);
    public static final Supplier<MapCodec<SerializableCorridorValidator.Inverted>> NOT_CORRIDOR_VALIDATOR = CORRIDOR_VALIDATORS
            .register("not", () -> SerializableCorridorValidator.Inverted.CODEC);
    public static final Supplier<MapCodec<SerializableCorridorValidator.GeneratorLayout>> GENERATOR_CORRIDOR_VALIDATOR = CORRIDOR_VALIDATORS
            .register("by_generator_layout", () -> SerializableCorridorValidator.GeneratorLayout.CODEC);
    public static final Supplier<MapCodec<SerializableCorridorValidator.And>> AND_CORRIDOR_VALIDATOR = CORRIDOR_VALIDATORS
            .register("and", () -> SerializableCorridorValidator.And.CODEC);
    public static final Supplier<MapCodec<SerializableCorridorValidator.Or>> OR_CORRIDOR_VALIDATOR = CORRIDOR_VALIDATORS
            .register("or", () -> SerializableCorridorValidator.Or.CODEC);
    public static final Supplier<MapCodec<SerializableCorridorValidator.Opposite>> OPPOSITE_CORRIDOR_VALIDATOR = CORRIDOR_VALIDATORS
            .register("opposite", () -> SerializableCorridorValidator.Opposite.CODEC);

}
