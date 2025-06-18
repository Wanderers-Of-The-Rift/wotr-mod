package com.wanderersoftherift.wotr.world.level.levelgen.layout.shape;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record SphereRiftShape(double radius) implements RiftShape {

    public static final MapCodec<SphereRiftShape> CODEC = RecordCodecBuilder.mapCodec(it -> it.group(
            Codec.DOUBLE.fieldOf("radius").forGetter(SphereRiftShape::radius)
    ).apply(it, SphereRiftShape::new));

    @Override
    public MapCodec<? extends RiftShape> codec() {
        return CODEC;
    }

    @Override
    public double chaosiveness(double x, double z) {
        return Double.POSITIVE_INFINITY;
    }

    // 2 = chaotic, 1 = unstable, 0 = stable
    @Override
    public int categorize(double x, double y) {
        return 2;
    }

    @Override
    public boolean isPositionValid(int x, int y, int z) {
        return ((x * x) + (y * y) + (z * z)) < (radius * radius);
    }
}
