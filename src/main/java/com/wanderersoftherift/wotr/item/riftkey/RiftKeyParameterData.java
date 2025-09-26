package com.wanderersoftherift.wotr.item.riftkey;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.wanderersoftherift.wotr.core.rift.parameter.definitions.RiftParameter;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.serialization.MutableMapCodec;
import net.minecraft.resources.ResourceKey;

import java.util.Collections;
import java.util.Map;

public record RiftKeyParameterData(Map<ResourceKey<RiftParameter>, Double> parameters) {
    public static final Codec<RiftKeyParameterData> CODEC = MutableMapCodec
            .of(Codec.unboundedMap(ResourceKey.codec(WotrRegistries.Keys.RIFT_PARAMETER_CONFIGS), Codec.DOUBLE))
            .xmap(RiftKeyParameterData::new, RiftKeyParameterData::parameters);

    public RiftKeyParameterData() {
        this(Collections.emptyMap());
    }

    public Double getParameter(ResourceKey<RiftParameter> key) {
        return parameters.get(key);
    }

    public RiftKeyParameterData withParameter(ResourceKey<RiftParameter> key, double value) {
        var newParams = builderWithoutParameter(key);
        newParams.put(key, value);
        return new RiftKeyParameterData(newParams.build());
    }

    public RiftKeyParameterData withoutParameter(ResourceKey<RiftParameter> typeHolder) {
        return new RiftKeyParameterData(builderWithoutParameter(typeHolder).build());
    }

    private ImmutableMap.Builder<ResourceKey<RiftParameter>, Double> builderWithoutParameter(
            ResourceKey<RiftParameter> typeHolder) {
        var newParams = ImmutableMap.<ResourceKey<RiftParameter>, Double>builder();
        parameters.entrySet().stream().filter(it -> !it.getKey().equals(typeHolder)).forEach(newParams::put);
        return newParams;
    }
}
