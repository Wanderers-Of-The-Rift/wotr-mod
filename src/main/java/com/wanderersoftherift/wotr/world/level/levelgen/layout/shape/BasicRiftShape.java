package com.wanderersoftherift.wotr.world.level.levelgen.layout.shape;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class BasicRiftShape implements RiftShape {

    public static final MapCodec<BasicRiftShape> CODEC = RecordCodecBuilder
            .mapCodec(it -> it.point(new BasicRiftShape()));

    @Override
    public MapCodec<? extends RiftShape> codec() {
        return CODEC;
    }

    @Override
    public double chaosiveness(double x, double z) {
        return 1 + 1.5 * Math.cosh(0.11 * Math.sqrt(x * x + z * z));
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
