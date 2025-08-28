package com.wanderersoftherift.wotr.world.level.levelgen.layout;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.core.rift.RiftConfig;
import com.wanderersoftherift.wotr.world.level.FastRiftGenerator;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.shape.BoxedRiftShape;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.shape.CoarseDiamondRiftShape;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.shape.RiftShape;
import net.minecraft.core.Vec3i;
import net.minecraft.server.MinecraftServer;

import java.util.List;

public record DefaultLayoutFactory(List<LayeredRiftLayout.LayoutLayer.Factory> layers)
        implements LayeredRiftLayout.Factory {
    public static final MapCodec<DefaultLayoutFactory> CODEC = LayeredRiftLayout.LayoutLayer.Factory.CODEC.listOf()
            .fieldOf("layers")
            .xmap(DefaultLayoutFactory::new, DefaultLayoutFactory::layers);
    private static final int SWITCH_TO_INFINITE_TIER_THRESHOLD = 16;
    private static final int DEFAULT_RIFT_HEIGHT_IN_CHUNKS = 24;

    @Override
    public LayeredRiftLayout.Factory withLayers(List<LayeredRiftLayout.LayoutLayer.Factory> layers) {
        return new DefaultLayoutFactory(layers());
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
            factory = new LayeredFiniteRiftLayout.Factory(boxedRiftShape, riftConfig.riftGen().seed(), layers);
        } else {
            factory = new LayeredInfiniteRiftLayout.Factory(shape, riftConfig.riftGen().seed(), layers);
        }
        return factory.createLayout(server, riftConfig);
    }

    @Override
    public RiftShape riftShape(RiftConfig riftConfig) {
        var layerCount = DEFAULT_RIFT_HEIGHT_IN_CHUNKS - FastRiftGenerator.MARGIN_LAYERS;
        var tier = riftConfig.tier();
        var baseShapeSize = 2 + tier * 3; // Y-position of highest room in the rift
        var baseShapeSlope = 3.0; // slope of the shape, actual value is 3× what you'd expect
        var baseRiftShape = new CoarseDiamondRiftShape(baseShapeSize, baseShapeSlope, layerCount);
        if (tier > SWITCH_TO_INFINITE_TIER_THRESHOLD) {
            return baseRiftShape;
        }
        var boxStartValue = -1 - 3 * tier;
        var boxSizeValue = 3 + 6 * tier;
        var boxStart = new Vec3i(boxStartValue, -layerCount / 2, boxStartValue);
        var boxSize = new Vec3i(boxSizeValue, layerCount, boxSizeValue);
        return new BoxedRiftShape(baseRiftShape, boxStart, boxSize);
    }
}
