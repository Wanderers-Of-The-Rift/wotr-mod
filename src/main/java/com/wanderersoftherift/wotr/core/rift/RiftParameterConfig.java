package com.wanderersoftherift.wotr.core.rift;

import com.mojang.serialization.Codec;

import java.util.List;

public record RiftParameterConfig(List<Double> values) {
    public static final Codec<RiftParameterConfig> CODEC = Codec.DOUBLE.listOf()
            .xmap(RiftParameterConfig::new, RiftParameterConfig::values);

    public double getValue(int tier) {
        if (tier >= values.size()) {
            return values.getLast();
        }
        return values.get(tier);
    }
}
