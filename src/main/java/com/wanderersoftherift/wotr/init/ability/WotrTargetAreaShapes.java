package com.wanderersoftherift.wotr.init.ability;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.targeting.shape.CubeShape;
import com.wanderersoftherift.wotr.abilities.targeting.shape.CuboidShape;
import com.wanderersoftherift.wotr.abilities.targeting.shape.SphereShape;
import com.wanderersoftherift.wotr.abilities.targeting.shape.TargetAreaShape;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class WotrTargetAreaShapes {

    public static final DeferredRegister<MapCodec<? extends TargetAreaShape>> SHAPES = DeferredRegister
            .create(WotrRegistries.Keys.TARGET_AREA_SHAPES, WanderersOfTheRift.MODID);

    public static final Supplier<MapCodec<? extends TargetAreaShape>> CUBE = SHAPES.register("cube",
            () -> CubeShape.CODEC);
    public static final Supplier<MapCodec<? extends TargetAreaShape>> CUBOID = SHAPES.register("cuboid",
            () -> CuboidShape.CODEC);
    public static final Supplier<MapCodec<? extends TargetAreaShape>> SPHERE = SHAPES.register("sphere",
            () -> SphereShape.CODEC);
}
