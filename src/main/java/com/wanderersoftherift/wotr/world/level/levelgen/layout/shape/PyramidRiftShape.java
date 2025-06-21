package com.wanderersoftherift.wotr.world.level.levelgen.layout.shape;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record PyramidRiftShape(double offset, double scaleY) implements RiftShape {

    public static final MapCodec<PyramidRiftShape> CODEC = RecordCodecBuilder.mapCodec(it -> it.group(
            Codec.DOUBLE.fieldOf("offset").forGetter(PyramidRiftShape::offset),
            Codec.DOUBLE.fieldOf("scale_y").forGetter(PyramidRiftShape::scaleY)
    ).apply(it, PyramidRiftShape::new));

    @Override
    public MapCodec<? extends RiftShape> codec() {
        return CODEC;
    }

    @Override
    public double riftHeightAt(double x, double z) {
        return offset - scaleY * Double.max(Math.abs(x), Math.abs(z));
    }
}
