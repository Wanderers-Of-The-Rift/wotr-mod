package com.wanderersoftherift.wotr.core.rift.parameter.definitions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;

import java.util.List;
import java.util.function.Function;

public record TableRiftParameter(List<Double> values) implements RegisteredRiftParameter {
    public static final MapCodec<TableRiftParameter> CODEC = Codec.DOUBLE.listOf()
            .xmap(TableRiftParameter::new, TableRiftParameter::values)
            .fieldOf("values");

    public double getValue(int tier, RandomSource rng, Function<ResourceKey<RiftParameter>, Double> parameterGetter) {
        if (tier >= values.size()) {
            return values.getLast();
        }
        return values.get(tier);
    }

    @Override
    public MapCodec<? extends RegisteredRiftParameter> getCodec() {
        return CODEC;
    }
}
