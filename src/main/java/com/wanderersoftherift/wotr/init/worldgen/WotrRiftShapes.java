package com.wanderersoftherift.wotr.init.worldgen;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.shape.BasicRiftShape;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.shape.BoxedRiftShape;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.shape.RiftShape;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class WotrRiftShapes {
    public static final DeferredRegister<MapCodec<? extends RiftShape>> RIFT_SHAPES = DeferredRegister
            .create(WotrRegistries.RIFT_SHAPE_TYPES, WanderersOfTheRift.MODID);

    public static final Supplier<MapCodec<BoxedRiftShape>> BOXED_RIFT_SHAPE = RIFT_SHAPES.register("boxed_shape",
            () -> BoxedRiftShape.CODEC);

    public static final Supplier<MapCodec<BasicRiftShape>> BASIC_RIFT_SHAPE = RIFT_SHAPES.register("basic_shape",
            () -> BasicRiftShape.CODEC);

    public static final Supplier<MapCodec<BasicRiftShape>> UNLIMITED_RIFT_SHAPE = RIFT_SHAPES
            .register("unlimited_shape", () -> BasicRiftShape.CODEC);

}
