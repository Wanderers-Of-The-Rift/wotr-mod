package com.wanderersoftherift.wotr.core.rift;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.wanderersoftherift.wotr.serialization.MutableMapCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.Collections;
import java.util.Map;

public record RiftKeyParameterData(Map<ResourceLocation, Double> parameters) {
    public static final Codec<RiftKeyParameterData> CODEC = MutableMapCodec
            .of(Codec.unboundedMap(ResourceLocation.CODEC, Codec.DOUBLE))
            .xmap(RiftKeyParameterData::new, RiftKeyParameterData::parameters);

    public RiftKeyParameterData() {
        this(Collections.emptyMap());
    }

    public Double getParameter(ResourceLocation key) {
        return parameters.get(key);
    }

    public RiftKeyParameterData withParameter(ResourceLocation key, double value) {
        var newParams = builderWithoutParameter(key);
        newParams.put(key, value);
        return new RiftKeyParameterData(newParams.build());
    }

    public RiftKeyParameterData withoutParameter(ResourceLocation typeHolder) {
        return new RiftKeyParameterData(builderWithoutParameter(typeHolder).build());
    }

    private ImmutableMap.Builder<ResourceLocation, Double> builderWithoutParameter(ResourceLocation typeHolder) {
        var newParams = ImmutableMap.<ResourceLocation, Double>builder();
        parameters.entrySet().stream().filter(it -> !it.getKey().equals(typeHolder)).forEach(newParams::put);
        return newParams;
    }
}
