package com.wanderersoftherift.wotr.item.runegem;


import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.RandomSource;

import java.util.function.IntFunction;

public enum RunegemShape {

    CIRCLE(0, "circle"),
    SQUARE(1, "square"),
    TRIANGLE(2, "triangle"),
    DIAMOND(3, "diamond"),
    HEART(4, "heart"),
    PENTAGON(5, "pentagon");

    public static final Codec<RunegemShape> CODEC = Codec.STRING.flatComapMap(s -> RunegemShape.byName(s, null), d -> DataResult.success(d.getName()));
    public static final IntFunction<RunegemShape> BY_ID = ByIdMap.continuous(RunegemShape::getId, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
    public static final StreamCodec<ByteBuf, RunegemShape> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, RunegemShape::getId);

    private final int id;
    private final String name;

    RunegemShape(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static RunegemShape byName(String name, RunegemShape defaultReturn) {
        for (RunegemShape value : values()) {
            if (value.name.equalsIgnoreCase(name)) {
                return value;
            }
        }
        return defaultReturn;
    }

    public static RunegemShape getRandomShape(RandomSource random) {
        return BY_ID.apply(random.nextInt(RunegemShape.values().length));
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
