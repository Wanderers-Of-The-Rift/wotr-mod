package com.wanderersoftherift.wotr.world.level.levelgen.layout.shape;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record CoarsePyramidRiftShape(double offset, double scaleY, int levelCount) implements RiftShape {

    public static final MapCodec<CoarsePyramidRiftShape> CODEC = RecordCodecBuilder.mapCodec(it -> it.group(
            Codec.DOUBLE.fieldOf("offset").forGetter(CoarsePyramidRiftShape::offset),
            Codec.DOUBLE.fieldOf("scale_y").forGetter(CoarsePyramidRiftShape::scaleY),
            Codec.INT.fieldOf("level_count").forGetter(RiftShape::levelCount)
    ).apply(it, CoarsePyramidRiftShape::new));

    @Override
    public MapCodec<? extends RiftShape> codec() {
        return CODEC;
    }

    @Override
    public double riftHeightAt(double x, double z) {
        return offset - scaleY * Double.max(Math.abs(Math.floor((x + 1) / 3)), Math.abs(Math.floor((z + 1) / 3)));
    }
}
