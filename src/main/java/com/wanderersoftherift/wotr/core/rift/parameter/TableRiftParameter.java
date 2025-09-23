package com.wanderersoftherift.wotr.core.rift.parameter;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

import java.util.List;

public record TableRiftParameter(List<Double> values) implements RegisteredRiftParameter {
    public static final MapCodec<TableRiftParameter> CODEC = Codec.DOUBLE.listOf()
            .xmap(TableRiftParameter::new, TableRiftParameter::values)
            .fieldOf("values");

    public double getValue(int tier) {
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
