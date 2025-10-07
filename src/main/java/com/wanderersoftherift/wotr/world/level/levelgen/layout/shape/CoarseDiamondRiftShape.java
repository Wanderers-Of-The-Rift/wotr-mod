package com.wanderersoftherift.wotr.world.level.levelgen.layout.shape;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record CoarseDiamondRiftShape(double offset, double scaleY, int levelCount) implements RiftShape {

    public static final MapCodec<CoarseDiamondRiftShape> CODEC = RecordCodecBuilder.mapCodec(it -> it.group(
            Codec.DOUBLE.fieldOf("offset").forGetter(CoarseDiamondRiftShape::offset),
            Codec.DOUBLE.fieldOf("scale_y").forGetter(CoarseDiamondRiftShape::scaleY),
            Codec.INT.fieldOf("level_count").forGetter(RiftShape::levelCount)
    ).apply(it, CoarseDiamondRiftShape::new));

    @Override
    public MapCodec<? extends RiftShape> codec() {
        return CODEC;
    }

    @Override
    public double riftHeightAt(double x, double z) {
        return offset - 3.0 * scaleY * (Math.abs(Math.floor((x + 1) / 3)) + Math.abs(Math.floor((z + 1) / 3)));
    }
}
