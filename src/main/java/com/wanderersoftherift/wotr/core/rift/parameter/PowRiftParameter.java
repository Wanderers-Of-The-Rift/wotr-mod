package com.wanderersoftherift.wotr.core.rift.parameter;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record PowRiftParameter(RiftParameter base, RiftParameter exp) implements RegisteredRiftParameter {
    public static final MapCodec<PowRiftParameter> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            RiftParameter.CODEC.optionalFieldOf("base", TierRiftParameter.INSTANCE).forGetter(PowRiftParameter::base),
            RiftParameter.CODEC.optionalFieldOf("exponent", TierRiftParameter.INSTANCE)
                    .forGetter(PowRiftParameter::exp))
            .apply(instance, PowRiftParameter::new));

    @Override
    public double getValue(int tier) {
        return Math.pow(base().getValue(tier), exp().getValue(tier));
    }

    @Override
    public MapCodec<? extends RegisteredRiftParameter> getCodec() {
        return CODEC;
    }
}
