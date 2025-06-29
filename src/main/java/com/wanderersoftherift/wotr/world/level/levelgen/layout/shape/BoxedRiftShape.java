package com.wanderersoftherift.wotr.world.level.levelgen.layout.shape;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Vec3i;

public record BoxedRiftShape(RiftShape baseShape, Vec3i boxStart, Vec3i boxSize) implements FiniteRiftShape {

    public static final MapCodec<BoxedRiftShape> CODEC = RecordCodecBuilder.mapCodec(it -> it.group(
            Codec.INT.fieldOf("start_x").forGetter(shape -> shape.boxStart.getX()),
            Codec.INT.fieldOf("start_y").forGetter(shape -> shape.boxStart.getY()),
            Codec.INT.fieldOf("start_z").forGetter(shape -> shape.boxStart.getZ()),
            Codec.INT.fieldOf("size_x").forGetter(shape -> shape.boxSize.getX()),
            Codec.INT.fieldOf("size_y").forGetter(shape -> shape.boxSize.getY()),
            Codec.INT.fieldOf("size_z").forGetter(shape -> shape.boxSize.getZ()),
            RiftShape.CODEC.fieldOf("base_shape").forGetter(BoxedRiftShape::baseShape)
    )
            .apply(it, (x1, y1, z1, x2, y2, z2, baseShape) -> BoxedRiftShape.of(baseShape, new Vec3i(x1, y1, z1),
                    new Vec3i(x2, y2, z2))));

    public static BoxedRiftShape of(RiftShape baseShape, Vec3i boxStart, Vec3i boxSize) {
        if (baseShape instanceof FiniteRiftShape baseBoxShape) {

            var baseStart = baseBoxShape.getBoxStart();
            var baseEnd = baseStart.offset(baseBoxShape.getBoxSize());
            var newStart = boxStart;
            var newEnd = newStart.offset(boxSize);
            var overlapStart = new Vec3i(Integer.max(newStart.getX(), baseStart.getX()),
                    Integer.max(newStart.getY(), baseStart.getY()), Integer.max(newStart.getZ(), baseStart.getZ()));
            var overlapEnd = new Vec3i(Integer.min(baseEnd.getX(), newEnd.getX()),
                    Integer.min(baseEnd.getY(), newEnd.getY()), Integer.min(baseEnd.getZ(), newEnd.getZ()));
            var overlapSize = overlapEnd.offset(overlapStart.multiply(-1));
            if (baseBoxShape instanceof BoxedRiftShape boxedRiftShape) {
                return of(boxedRiftShape.baseShape, overlapStart, overlapSize);
            }
            return new BoxedRiftShape(baseBoxShape, overlapStart, overlapSize);
        }
        return new BoxedRiftShape(baseShape, boxStart, boxSize);
    }

    @Override
    public boolean isPositionValid(int x, int y, int z) {
        var start = getBoxStart();
        var end = start.offset(getBoxSize());
        return baseShape.isPositionValid(x, y, z) && end.getX() > x && start.getX() <= x
                && Integer.min(end.getY(), baseShape.levelCount() / 2) > y
                && Integer.max(start.getY(), -baseShape.levelCount() / 2) <= y && end.getZ() > z && start.getZ() <= z;
    }

    @Override
    public int levelCount() {
        return Integer.min(baseShape.levelCount(),
                2 * Integer.max(Math.abs(boxStart.getY()), Math.abs(boxStart.getY() + boxSize.getY())));
    }

    @Override
    public MapCodec<? extends RiftShape> codec() {
        return CODEC;
    }

    @Override
    public double riftHeightAt(double x, double z) {
        return baseShape.riftHeightAt(x, z);
    }

    @Override
    public Vec3i getBoxStart() {
        return boxStart;
    }

    @Override
    public Vec3i getBoxSize() {
        return boxSize;
    }
}
