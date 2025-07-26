package com.wanderersoftherift.wotr.core.rift.predicate;

import com.mojang.serialization.Codec;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.item.riftkey.RiftConfig;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record RiftTierPredicate(int tier) implements RiftConfigPredicate {
    public static final Codec<RiftTierPredicate> CODEC = Codec.INT.xmap(RiftTierPredicate::new,
            RiftTierPredicate::tier);

    public static final StreamCodec<ByteBuf, RiftTierPredicate> STREAM_CODEC = ByteBufCodecs.INT
            .map(RiftTierPredicate::new, RiftTierPredicate::tier);

    @Override
    public boolean match(RiftConfig config) {
        return config.tier() >= tier;
    }

    @Override
    public MutableComponent displayText() {
        return Component.translatable(WanderersOfTheRift.translationId("goal", "rift.tier"), tier);
    }
}
