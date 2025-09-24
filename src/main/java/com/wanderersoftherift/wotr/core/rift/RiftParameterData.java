package com.wanderersoftherift.wotr.core.rift;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.rift.parameter.RiftParameter;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.init.WotrDataComponentType;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.serialization.MutableMapCodec;
import com.wanderersoftherift.wotr.util.RandomSourceFromJavaRandom;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public record RiftParameterData(Map<ResourceLocation, RiftParameterInstance> parameters) {
    public static final Codec<RiftParameterData> CODEC = MutableMapCodec
            .of(Codec.unboundedMap(ResourceLocation.CODEC, RiftParameterInstance.CODEC))
            .xmap(RiftParameterData::new, RiftParameterData::parameters);

    public static final RiftConfigDataType<RiftParameterData> RIFT_CONFIG_DATA_TYPE = RiftConfigDataType.create(CODEC,
            RiftParameterData::initialize);

    private static final long SALT = 676869343536951L;

    public RiftParameterData() {
        this(new HashMap<>());
    }

    private static RiftParameterData initialize(ItemStack itemStack, Long seed, RegistryAccess registryAccess) {

        var registry = registryAccess.lookupOrThrow(WotrRegistries.Keys.RIFT_PARAMETER_CONFIGS);
        var params = ImmutableMap.<ResourceLocation, RiftParameterInstance>builder();
        var tierOptional = itemStack.get(WotrDataComponentType.RiftKeyData.RIFT_TIER);
        var tier = Objects.requireNonNullElse(tierOptional, 0);
        var rng = RandomSourceFromJavaRandom.positional(RandomSourceFromJavaRandom.get(0), seed + SALT);
        var riftKeyParameters = itemStack.get(WotrDataComponentType.RiftKeyData.RIFT_PARAMETERS);
        registry.asHolderIdMap().forEach(it -> {
            var param = new RiftParameterInstance();
            var key = it.getKey().location();
            try {
                var baseValue = getValueFor(it.value(), rng, tier, key, registry, riftKeyParameters);
                param.setBase(baseValue);
            } catch (StackOverflowError e) {
                WanderersOfTheRift.LOGGER.error("definition of rift parameter {} contains reference loop!", key);
            }
            params.put(key, param);
        });
        return new RiftParameterData(params.build());
    }

    private static double getValueFor(
            RiftParameter config,
            PositionalRandomFactory rng,
            int tier,
            ResourceLocation key,
            Registry<RiftParameter> registry,
            RiftKeyParameterData riftKeyParameters) {
        if (riftKeyParameters != null) {
            var keyValue = riftKeyParameters.getParameter(key);
            if (keyValue != null) {
                return keyValue;
            }
        }
        return config.getValue(tier, rng.fromHashOf(key),
                (key2) -> getValueFor(registry.getValue(key2), rng, tier, key2, registry, riftKeyParameters));
    }

    public static RiftParameterData forLevel(ServerLevel level) {
        return level.getData(WotrAttachments.RIFT_PARAMETER_DATA);
    }

    public RiftParameterInstance getParameter(ResourceLocation typeHolder) {
        return parameters.get(typeHolder);
    }
}
