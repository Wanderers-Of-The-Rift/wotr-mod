package com.wanderersoftherift.wotr.world.level.levelgen.layout.shape;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class UnlimitedRiftShape implements RiftShape {

    public static final MapCodec<UnlimitedRiftShape> CODEC = RecordCodecBuilder
            .mapCodec(it -> it.point(new UnlimitedRiftShape()));

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
        return true;
    }
}
