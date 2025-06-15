package com.wanderersoftherift.wotr.world.level.levelgen.layout.shape;

import net.minecraft.core.Vec3i;

import static java.lang.Math.abs;

public interface RiftShape {

    @Deprecated
    double chaosiveness(double x, double z);

    // 2 = chaotic, 1 = unstable, 0 = stable
    int categorize(double x, double y);

    default boolean isPositionValid(int x, int y, int z) {
        return chaosiveness(x, z) > abs(y);
    }

    static FiniteRiftShape boxed(RiftShape baseShape, Vec3i boxStart, Vec3i boxSize) {

        return new FiniteRiftShape() {
            @Override
            public Vec3i getBoxStart() {
                return boxStart;
            }

            @Override
            public Vec3i getBoxSize() {
                return boxSize;
            }

            @Override
            public double chaosiveness(double x, double z) {
                return baseShape.chaosiveness(x, z);
            }

            @Override
            public int categorize(double x, double y) {
                return baseShape.categorize(x, y);
            }
        };
    }
}
