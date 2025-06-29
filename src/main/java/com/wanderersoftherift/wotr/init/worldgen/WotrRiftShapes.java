package com.wanderersoftherift.wotr.init.worldgen;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.shape.BoxedRiftShape;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.shape.CoarseDiamondRiftShape;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.shape.CoarsePyramidRiftShape;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.shape.DiamondRiftShape;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.shape.ExponentialRiftShape;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.shape.InverseExponentialRiftShape;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.shape.PyramidRiftShape;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.shape.RiftShape;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.shape.SphereRiftShape;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.shape.UnlimitedRiftShape;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class WotrRiftShapes {
    public static final DeferredRegister<MapCodec<? extends RiftShape>> RIFT_SHAPES = DeferredRegister
            .create(WotrRegistries.RIFT_SHAPE_TYPES, WanderersOfTheRift.MODID);

    public static final Supplier<MapCodec<BoxedRiftShape>> BOXED_RIFT_SHAPE = RIFT_SHAPES.register("boxed_shape",
            () -> BoxedRiftShape.CODEC);

    public static final Supplier<MapCodec<ExponentialRiftShape>> EXPONENTIAL_RIFT_SHAPE = RIFT_SHAPES
            .register("exponential_shape", () -> ExponentialRiftShape.CODEC);

    public static final Supplier<MapCodec<UnlimitedRiftShape>> UNLIMITED_RIFT_SHAPE = RIFT_SHAPES
            .register("unlimited_shape", () -> UnlimitedRiftShape.CODEC);
    public static final Supplier<MapCodec<InverseExponentialRiftShape>> INVERSE_EXPONENTIAL_RIFT_SHAPE = RIFT_SHAPES
            .register("inverse_exponential_shape", () -> InverseExponentialRiftShape.CODEC);
    public static final Supplier<MapCodec<DiamondRiftShape>> DIAMOND_RIFT_SHAPE = RIFT_SHAPES.register("diamond_shape",
            () -> DiamondRiftShape.CODEC);
    public static final Supplier<MapCodec<PyramidRiftShape>> PYRAMID_RIFT_SHAPE = RIFT_SHAPES.register("pyramid_shape",
            () -> PyramidRiftShape.CODEC);
    public static final Supplier<MapCodec<CoarseDiamondRiftShape>> COARSE_DIAMOND_RIFT_SHAPE = RIFT_SHAPES
            .register("coarse_diamond_shape", () -> CoarseDiamondRiftShape.CODEC);
    public static final Supplier<MapCodec<CoarsePyramidRiftShape>> COARSE_PYRAMID_RIFT_SHAPE = RIFT_SHAPES
            .register("coarse_pyramid_shape", () -> CoarsePyramidRiftShape.CODEC);
    public static final Supplier<MapCodec<SphereRiftShape>> SPHERE_RIFT_SHAPE = RIFT_SHAPES.register("sphere_shape",
            () -> SphereRiftShape.CODEC);

}
