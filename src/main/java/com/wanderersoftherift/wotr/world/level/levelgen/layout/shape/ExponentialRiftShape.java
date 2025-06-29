package com.wanderersoftherift.wotr.world.level.levelgen.layout.shape;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record ExponentialRiftShape(double offset, double scaleY, double scaleXZ, int levelCount) implements RiftShape {

    public static final MapCodec<ExponentialRiftShape> CODEC = RecordCodecBuilder.mapCodec(it -> it.group(
            Codec.DOUBLE.fieldOf("offset").forGetter(ExponentialRiftShape::offset),
            Codec.DOUBLE.fieldOf("scale_y").forGetter(ExponentialRiftShape::scaleY),
            Codec.DOUBLE.fieldOf("scale_xz").forGetter(ExponentialRiftShape::scaleXZ),
            Codec.INT.fieldOf("level_count").forGetter(RiftShape::levelCount)
    ).apply(it, ExponentialRiftShape::new));

    @Override
    public MapCodec<? extends RiftShape> codec() {
        return CODEC;
    }

    @Override
    public double riftHeightAt(double x, double z) {
        return offset + scaleY * Math.cosh(scaleXZ * Math.sqrt(x * x + z * z));
    }
}
