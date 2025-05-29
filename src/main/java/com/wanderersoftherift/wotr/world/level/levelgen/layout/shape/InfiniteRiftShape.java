package com.wanderersoftherift.wotr.world.level.levelgen.layout.shape;

public interface InfiniteRiftShape {

    double chaosiveness(double x, double z);

    // 2 = chaotic, 1 = unstable, 0 = stable
    int categorize(double x, double y);
}
