package com.wanderersoftherift.wotr.world.level.levelgen.layout.shape;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record CoarsePyramidRiftShape(double offset, double scaleY) implements RiftShape {

    public static final MapCodec<CoarsePyramidRiftShape> CODEC = RecordCodecBuilder.mapCodec(it -> it.group(
            Codec.DOUBLE.fieldOf("offset").forGetter(CoarsePyramidRiftShape::offset),
            Codec.DOUBLE.fieldOf("scale_y").forGetter(CoarsePyramidRiftShape::scaleY)
    ).apply(it, CoarsePyramidRiftShape::new));

    @Override
    public MapCodec<? extends RiftShape> codec() {
        return CODEC;
    }

    @Override
    public double chaosiveness(double x, double z) {
        return offset - scaleY * Double.max(Math.abs(Math.floor((x + 1) / 3)), Math.abs(Math.floor((z + 1) / 3)));
    }

    // 2 = chaotic, 1 = unstable, 0 = stable
    @Override
    public int categorize(double x, double y) {
        var chaosiveness = chaosiveness(x, y);
        if (chaosiveness > 2.5) {
            return 2;
        } else {
            if (chaosiveness > 1.75) {
                return 1;
            } else {
                return 0;
            }
        }
    }
}
