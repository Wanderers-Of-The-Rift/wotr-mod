package com.wanderersoftherift.wotr.item.ability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;

public record Cooldown(long from, long until) {
    public static final Codec<Cooldown> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.LONG.fieldOf("from").forGetter(Cooldown::from), Codec.LONG.fieldOf("until").forGetter(Cooldown::until)
    ).apply(instance, Cooldown::new));

    public static final StreamCodec<ByteBuf, Cooldown> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.LONG, Cooldown::from, ByteBufCodecs.LONG, Cooldown::until, Cooldown::new);

    public Cooldown() {
        this(Long.MIN_VALUE, Long.MIN_VALUE);
    }

    public boolean onCooldown(Level level) {
        return level.getGameTime() < until;
    }

    public long remaining(Level level) {
        return Math.min(0, until - level.getGameTime());
    }

    public float remainingFraction(Level level) {
        if (until == from) {
            return 0f;
        }
        return Math.clamp((float) (until - level.getGameTime()) / (until - from), 0f, 1f);
    }
}
