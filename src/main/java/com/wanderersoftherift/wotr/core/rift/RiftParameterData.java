package com.wanderersoftherift.wotr.core.rift;

import com.mojang.serialization.Codec;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.serialization.MutableMapCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;

import java.util.HashMap;

public record RiftParameterData(HashMap<ResourceLocation, RiftParameterInstance> parameters) {
    public static final Codec<RiftParameterData> CODEC = MutableMapCodec
            .of(Codec.unboundedMap(ResourceLocation.CODEC, RiftParameterInstance.CODEC))
            .xmap(RiftParameterData::new, RiftParameterData::parameters);

    public RiftParameterData() {
        this(new HashMap<>());
    }

    public static RiftParameterData forLevel(ServerLevel level) {
        return level.getData(WotrAttachments.RIFT_PARAMETER_DATA);
    }

    public RiftParameterInstance getParameter(ResourceLocation typeHolder) {
        return parameters.computeIfAbsent(typeHolder, key -> new RiftParameterInstance());
    }
}
