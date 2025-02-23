package com.dimensiondelvers.dimensiondelvers.item.runegem;


import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;

import java.util.function.IntFunction;

public enum RunegemTier {

    RAW(0, "raw"),
    SHAPED(1, "shaped"),
    CUT(2, "cut"),
    POLISHED(3, "polished"),
    FRAMED(4, "framed"),
    UNIQUE(5, "unique");

    public static final Codec<RunegemTier> CODEC = Codec.STRING.flatComapMap(s -> RunegemTier.byName(s, null), d -> DataResult.success(d.getName()));
    public static final IntFunction<RunegemTier> BY_ID = ByIdMap.continuous(RunegemTier::getId, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
    public static final StreamCodec<ByteBuf, RunegemTier> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, RunegemTier::getId);

    private final int id;
    private final String name;

    RunegemTier(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public static RunegemTier byName(String name, RunegemTier defaultReturn) {
        for (RunegemTier value : values()){
            if (value.name.equalsIgnoreCase(name)) {
                return value;
            }
        }
        return defaultReturn;
    }
}
