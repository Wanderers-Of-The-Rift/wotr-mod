package com.wanderersoftherift.wotr.world.level.levelgen.layout.shape;

import static java.lang.Math.abs;

public interface RiftShape {

    double chaosiveness(double x, double z);

    // 2 = chaotic, 1 = unstable, 0 = stable
    int categorize(double x, double y);

    default boolean isPositionValid(int x, int y, int z) {
        return chaosiveness(x, z) > abs(y);
    }
}
