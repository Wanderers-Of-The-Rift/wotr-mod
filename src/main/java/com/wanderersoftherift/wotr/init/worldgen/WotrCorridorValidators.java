package com.wanderersoftherift.wotr.init.worldgen;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.world.level.levelgen.space.corridor.And;
import com.wanderersoftherift.wotr.world.level.levelgen.space.corridor.GeneratorLayout;
import com.wanderersoftherift.wotr.world.level.levelgen.space.corridor.Identity;
import com.wanderersoftherift.wotr.world.level.levelgen.space.corridor.Inverted;
import com.wanderersoftherift.wotr.world.level.levelgen.space.corridor.Opposite;
import com.wanderersoftherift.wotr.world.level.levelgen.space.corridor.Or;
import com.wanderersoftherift.wotr.world.level.levelgen.space.corridor.SerializableCorridorValidator;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class WotrCorridorValidators {
    public static final DeferredRegister<MapCodec<? extends SerializableCorridorValidator>> CORRIDOR_VALIDATORS = DeferredRegister
            .create(WotrRegistries.RIFT_CORRIDOR_VALIDATORS, WanderersOfTheRift.MODID);

    public static final Supplier<MapCodec<Identity>> IDENTITY_CORRIDOR_VALIDATOR = CORRIDOR_VALIDATORS
            .register("identity", () -> Identity.CODEC);
    public static final Supplier<MapCodec<Inverted>> NOT_CORRIDOR_VALIDATOR = CORRIDOR_VALIDATORS.register("not",
            () -> Inverted.CODEC);
    public static final Supplier<MapCodec<GeneratorLayout>> GENERATOR_CORRIDOR_VALIDATOR = CORRIDOR_VALIDATORS
            .register("by_generator_layout", () -> GeneratorLayout.CODEC);
    public static final Supplier<MapCodec<And>> AND_CORRIDOR_VALIDATOR = CORRIDOR_VALIDATORS.register("and",
            () -> And.CODEC);
    public static final Supplier<MapCodec<Or>> OR_CORRIDOR_VALIDATOR = CORRIDOR_VALIDATORS.register("or",
            () -> Or.CODEC);
    public static final Supplier<MapCodec<Opposite>> OPPOSITE_CORRIDOR_VALIDATOR = CORRIDOR_VALIDATORS
            .register("opposite", () -> Opposite.CODEC);

}
