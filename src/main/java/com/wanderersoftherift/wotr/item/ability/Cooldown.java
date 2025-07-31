package com.wanderersoftherift.wotr.item.ability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record Cooldown(long until) {
    public static final Codec<Cooldown> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.LONG.fieldOf("until").forGetter(Cooldown::until)
    ).apply(instance, Cooldown::new));

    public static final StreamCodec<ByteBuf, Cooldown> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.LONG,
            Cooldown::until, Cooldown::new);
}
