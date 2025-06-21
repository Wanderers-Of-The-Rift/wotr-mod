package com.wanderersoftherift.wotr.world.level.levelgen.layout.shape;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record DiamondRiftShape(double offset, double scaleY) implements RiftShape {

    public static final MapCodec<DiamondRiftShape> CODEC = RecordCodecBuilder.mapCodec(it -> it.group(
            Codec.DOUBLE.fieldOf("offset").forGetter(DiamondRiftShape::offset),
            Codec.DOUBLE.fieldOf("scale_y").forGetter(DiamondRiftShape::scaleY)
    ).apply(it, DiamondRiftShape::new));

    @Override
    public MapCodec<? extends RiftShape> codec() {
        return CODEC;
    }

    @Override
    public double riftHeightAt(double x, double z) {
        return offset - scaleY * (Math.abs(x) + Math.abs(z));
    }
}
