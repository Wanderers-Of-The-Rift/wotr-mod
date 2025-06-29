package com.wanderersoftherift.wotr.world.level.levelgen.layout.shape;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record SphereRiftShape(double radius, int levelCount) implements RiftShape {

    public static final MapCodec<SphereRiftShape> CODEC = RecordCodecBuilder.mapCodec(it -> it.group(
            Codec.DOUBLE.fieldOf("radius").forGetter(SphereRiftShape::radius),
            Codec.INT.fieldOf("level_count").forGetter(RiftShape::levelCount)
    ).apply(it, SphereRiftShape::new));

    @Override
    public MapCodec<? extends RiftShape> codec() {
        return CODEC;
    }

    @Override
    public double riftHeightAt(double x, double z) {
        return Double.POSITIVE_INFINITY;
    }

    @Override
    public boolean isPositionValid(int x, int y, int z) {
        return ((x * x) + (y * y) + (z * z)) < (radius * radius);
    }
}
