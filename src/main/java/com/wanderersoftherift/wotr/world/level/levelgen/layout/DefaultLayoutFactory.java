package com.wanderersoftherift.wotr.world.level.levelgen.layout;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.rift.RiftConfig;
import com.wanderersoftherift.wotr.core.rift.parameter.definitions.RiftParameter;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.init.worldgen.WotrRiftConfigDataTypes;
import com.wanderersoftherift.wotr.world.level.FastRiftGenerator;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.shape.BoxedRiftShape;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.shape.CoarseDiamondRiftShape;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.shape.RiftShape;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;

import java.util.List;
import java.util.Optional;

public record DefaultLayoutFactory(List<LayeredRiftLayout.LayoutLayer.Factory> layers)
        implements LayeredRiftLayout.Factory {
    public static final MapCodec<DefaultLayoutFactory> CODEC = LayeredRiftLayout.LayoutLayer.Factory.CODEC.listOf()
            .fieldOf("layers")
            .xmap(DefaultLayoutFactory::new, DefaultLayoutFactory::layers);
    private static final int SWITCH_TO_INFINITE_TIER_THRESHOLD = 16;
    private static final int DEFAULT_RIFT_HEIGHT_IN_CHUNKS = 24;
    private static final ResourceKey<RiftParameter> BOX_START_PARAMETER_KEY = ResourceKey
            .create(WotrRegistries.Keys.RIFT_PARAMETER_CONFIGS, WanderersOfTheRift.id("rift_shape/box_start"));
    private static final ResourceKey<RiftParameter> BOX_SIZE_PARAMETER_KEY = ResourceKey
            .create(WotrRegistries.Keys.RIFT_PARAMETER_CONFIGS, WanderersOfTheRift.id("rift_shape/box_size"));
    private static final ResourceKey<RiftParameter> PYRAMID_HEIGHT_PARAMETER_KEY = ResourceKey
            .create(WotrRegistries.Keys.RIFT_PARAMETER_CONFIGS, WanderersOfTheRift.id("rift_shape/pyramid_height"));
    private static final ResourceKey<RiftParameter> PYRAMID_SLOPE_PARAMETER_KEY = ResourceKey
            .create(WotrRegistries.Keys.RIFT_PARAMETER_CONFIGS, WanderersOfTheRift.id("rift_shape/pyramid_slope"));

    @Override
    public LayeredRiftLayout.Factory withLayers(List<LayeredRiftLayout.LayoutLayer.Factory> layers) {
        return new DefaultLayoutFactory(layers);
    }

    @Override
    public MapCodec<? extends RiftLayout.Factory> codec() {
        return CODEC;
    }

    @Override
    public RiftLayout createLayout(MinecraftServer server, RiftConfig riftConfig) {
        var shape = riftShape(riftConfig);
        RiftLayout.Factory factory;
        if (shape instanceof BoxedRiftShape boxedRiftShape) {
            factory = new LayeredFiniteRiftLayout.Factory(boxedRiftShape, Optional.of(riftConfig.seed()), layers);
        } else {
            factory = new LayeredInfiniteRiftLayout.Factory(shape, Optional.of(riftConfig.seed()), layers);
        }
        return factory.createLayout(server, riftConfig);
    }

    @Override
    public RiftShape riftShape(RiftConfig riftConfig) {
        var layerCount = DEFAULT_RIFT_HEIGHT_IN_CHUNKS - FastRiftGenerator.MARGIN_LAYERS;
        var tier = riftConfig.tier();
        var parameters = riftConfig.getCustomData(WotrRiftConfigDataTypes.INITIAL_RIFT_PARAMETERS);
        var baseShapeSize = parameters.getParameter(PYRAMID_HEIGHT_PARAMETER_KEY).get();
        var baseShapeSlope = parameters.getParameter(PYRAMID_SLOPE_PARAMETER_KEY).get();
        var baseRiftShape = new CoarseDiamondRiftShape(baseShapeSize, baseShapeSlope, layerCount);
        if (tier > SWITCH_TO_INFINITE_TIER_THRESHOLD) {
            return baseRiftShape;
        }
        var boxStartValue = parameters.getParameter(BOX_START_PARAMETER_KEY).get();
        var boxSizeValue = parameters.getParameter(BOX_SIZE_PARAMETER_KEY).get();
        var boxStart = new Vec3i((int) boxStartValue, -layerCount / 2, (int) boxStartValue);
        var boxSize = new Vec3i((int) boxSizeValue, layerCount, (int) boxSizeValue);
        return new BoxedRiftShape(baseRiftShape, boxStart, boxSize);
    }
}
