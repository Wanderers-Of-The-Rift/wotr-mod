package com.wanderersoftherift.wotr.item.runegem;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.wanderersoftherift.wotr.init.WotrTags;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ByIdMap;

import java.util.function.IntFunction;

public enum RunegemTier {

    RAW(0, "raw", WotrTags.Runegems.RAW),
    CUT(1, "cut", WotrTags.Runegems.CUT),
    SHAPED(2, "shaped", WotrTags.Runegems.SHAPED),
    POLISHED(3, "polished", WotrTags.Runegems.POLISHED),
    FRAMED(4, "framed", WotrTags.Runegems.FRAMED),
    UNIQUE(5, "unique", WotrTags.Runegems.UNIQUE);

    public static final Codec<RunegemTier> CODEC = Codec.STRING.flatComapMap(s -> RunegemTier.byName(s, null),
            d -> DataResult.success(d.getName()));
    public static final IntFunction<RunegemTier> BY_ID = ByIdMap.continuous(RunegemTier::getId, values(),
            ByIdMap.OutOfBoundsStrategy.WRAP);
    public static final StreamCodec<ByteBuf, RunegemTier> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID,
            RunegemTier::getId);

    private final int id;
    private final String name;
    private final TagKey<RunegemData> tagKey;

    RunegemTier(int id, String name, TagKey<RunegemData> tagKey) {
        this.id = id;
        this.name = name;
        this.tagKey = tagKey;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public TagKey<RunegemData> getTagKey() {
        return tagKey;
    }

    public static RunegemTier byName(String name, RunegemTier defaultReturn) {
        for (RunegemTier value : values()) {
            if (value.name.equalsIgnoreCase(name)) {
                return value;
            }
        }
        return defaultReturn;
    }
}