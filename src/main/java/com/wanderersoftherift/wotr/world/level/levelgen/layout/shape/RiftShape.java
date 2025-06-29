package com.wanderersoftherift.wotr.world.level.levelgen.layout.shape;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.init.WotrRegistries;

import static java.lang.Math.abs;

public interface RiftShape {

    Codec<RiftShape> CODEC = WotrRegistries.RIFT_SHAPE_TYPES.byNameCodec()
            .dispatch(shape -> shape.codec(), codec -> codec);

    MapCodec<? extends RiftShape> codec();

    @Deprecated
    double riftHeightAt(double x, double z);

    default boolean isPositionValid(int x, int y, int z) {
        return Integer.min((int) riftHeightAt(x, z), levelCount()/2) > abs(y);
    }

    int levelCount();
}
