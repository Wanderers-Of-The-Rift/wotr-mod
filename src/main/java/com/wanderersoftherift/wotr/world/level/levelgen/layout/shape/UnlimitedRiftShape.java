package com.wanderersoftherift.wotr.world.level.levelgen.layout.shape;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record UnlimitedRiftShape(int levelCount) implements RiftShape {

    public static final MapCodec<UnlimitedRiftShape> CODEC = RecordCodecBuilder
            .mapCodec(it -> it.group(Codec.INT.fieldOf("level_count").forGetter(RiftShape::levelCount))
                    .apply(it, UnlimitedRiftShape::new));

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
