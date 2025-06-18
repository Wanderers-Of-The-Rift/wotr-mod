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
    public double chaosiveness(double x, double z) {
        return offset - scaleY * (Math.abs(x) + Math.abs(z));
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
