package com.wanderersoftherift.wotr.world.level.levelgen.layout.shape;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Vec3i;

public record BoxedRiftShape(RiftShape baseShape, Vec3i boxStart, Vec3i boxSize) implements FiniteRiftShape {

    public static final MapCodec<BoxedRiftShape> CODEC = RecordCodecBuilder.mapCodec(it -> it.group(
            Codec.INT.fieldOf("start_x").forGetter(it2 -> it2.boxStart.getX()),
            Codec.INT.fieldOf("start_y").forGetter(it2 -> it2.boxStart.getY()),
            Codec.INT.fieldOf("start_z").forGetter(it2 -> it2.boxStart.getZ()),
            Codec.INT.fieldOf("size_x").forGetter(it2 -> it2.boxSize.getX()),
            Codec.INT.fieldOf("size_y").forGetter(it2 -> it2.boxSize.getY()),
            Codec.INT.fieldOf("size_z").forGetter(it2 -> it2.boxSize.getZ()),
            RiftShape.CODEC.fieldOf("base_shape").forGetter(it2 -> it2.baseShape)
    )
            .apply(it, (x1, y1, z1, x2, y2, z2, baseShape) -> new BoxedRiftShape(baseShape, new Vec3i(x1, y1, z1),
                    new Vec3i(x2, y2, z2))));

    @Override
    public MapCodec<? extends RiftShape> codec() {
        return CODEC;
    }

    @Override
    public double chaosiveness(double x, double z) {
        return baseShape.chaosiveness(x, z);
    }

    @Override
    public int categorize(double x, double y) {
        return baseShape.categorize(x, y);
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
