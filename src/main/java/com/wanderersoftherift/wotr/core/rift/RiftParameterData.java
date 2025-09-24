package com.wanderersoftherift.wotr.core.rift;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.init.WotrDataComponentType;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.serialization.MutableMapCodec;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public record RiftParameterData(Map<ResourceLocation, RiftParameterInstance> parameters) {
    public static final Codec<RiftParameterData> CODEC = MutableMapCodec
            .of(Codec.unboundedMap(ResourceLocation.CODEC, RiftParameterInstance.CODEC))
            .xmap(RiftParameterData::new, RiftParameterData::parameters);

    public static final RiftConfigDataType<RiftParameterData> RIFT_CONFIG_DATA_TYPE = RiftConfigDataType.create(CODEC,
            RiftParameterData::initialize);

    public RiftParameterData() {
        this(new HashMap<>());
    }

    private static RiftParameterData initialize(ItemStack itemStack, Long seed, RegistryAccess registryAccess) {

        var registry = registryAccess.lookupOrThrow(WotrRegistries.Keys.RIFT_PARAMETER_CONFIGS);
        var params = ImmutableMap.<ResourceLocation, RiftParameterInstance>builder();
        var tierOptional = itemStack.get(WotrDataComponentType.RiftKeyData.RIFT_TIER);
        var tier = Objects.requireNonNullElse(tierOptional, 0);
        registry.asHolderIdMap().forEach(it -> {
            var baseValue = it.value().getValue(tier);
            var param = new RiftParameterInstance();
            param.setBase(baseValue);
            params.put(it.getKey().location(), param);
        });
        return new RiftParameterData(params.build());
    }

    public static RiftParameterData forLevel(ServerLevel level) {
        return level.getData(WotrAttachments.RIFT_PARAMETER_DATA);
    }

    public RiftParameterInstance getParameter(ResourceLocation typeHolder) {
        return parameters.get(typeHolder);
    }
}
