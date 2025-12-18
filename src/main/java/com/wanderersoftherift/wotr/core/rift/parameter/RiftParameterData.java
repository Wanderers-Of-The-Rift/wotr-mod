package com.wanderersoftherift.wotr.core.rift.parameter;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.rift.RiftConfigDataType;
import com.wanderersoftherift.wotr.core.rift.parameter.definitions.RiftParameter;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.init.WotrDataComponentType;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.item.riftkey.RiftKeyParameterData;
import com.wanderersoftherift.wotr.item.riftkey.RiftParameterModifierProvider;
import com.wanderersoftherift.wotr.serialization.MutableMapCodec;
import com.wanderersoftherift.wotr.util.RandomFactoryType;
import com.wanderersoftherift.wotr.util.RandomSourceFromJavaRandom;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public record RiftParameterData(Map<ResourceKey<RiftParameter>, RiftParameterInstance> parameters) {
    public static final Codec<RiftParameterData> CODEC = MutableMapCodec.of(Codec
            .unboundedMap(ResourceKey.codec(WotrRegistries.Keys.RIFT_PARAMETER_CONFIGS), RiftParameterInstance.CODEC))
            .xmap(RiftParameterData::new, RiftParameterData::parameters);

    public static final RiftConfigDataType<RiftParameterData> RIFT_CONFIG_DATA_TYPE = RiftConfigDataType.create(CODEC,
            RiftParameterData::initialize);

    private static final long SALT = 676869343536951L;

    public RiftParameterData() {
        this(new HashMap<>());
    }

    private static RiftParameterData initialize(ItemStack itemStack, Long seed, RegistryAccess registryAccess) {

        var registry = registryAccess.lookupOrThrow(WotrRegistries.Keys.RIFT_PARAMETER_CONFIGS);
        var params = ImmutableMap.<ResourceKey<RiftParameter>, RiftParameterInstance>builder();
        var tierOptional = itemStack.get(WotrDataComponentType.RiftKeyData.RIFT_TIER);
        var tier = Objects.requireNonNullElse(tierOptional, 0);
        var rng = RandomSourceFromJavaRandom.positional(RandomSourceFromJavaRandom.get(RandomFactoryType.DEFAULT),
                seed + SALT);
        var riftKeyParameters = itemStack.get(WotrDataComponentType.RiftKeyData.RIFT_PARAMETERS);
        var modifierProviders = itemStack.getAllOfType(RiftParameterModifierProvider.class)
                .flatMap(RiftParameterModifierProvider::getModifiers)
                .toList();
        registry.asHolderIdMap().forEach(it -> {
            var param = new RiftParameterInstance();
            var key = it.getKey();
            try {
                var baseValue = computeDefaultValueForParameter(it.value(), rng, tier, key, registry,
                        riftKeyParameters);
                param.setBase(baseValue);
            } catch (StackOverflowError e) {
                WanderersOfTheRift.LOGGER.error("definition of rift parameter {} contains reference loop!",
                        key.location());
            }
            for (var modifier : modifierProviders) {
                if (modifier.isApplicable(it)) {
                    modifier.enable(param);
                }
            }

            params.put(key, param);
        });
        return new RiftParameterData(params.build());
    }

    private static double computeDefaultValueForParameter(
            RiftParameter config,
            PositionalRandomFactory rng,
            int tier,
            ResourceKey<RiftParameter> key,
            Registry<RiftParameter> registry,
            RiftKeyParameterData riftKeyParameters) {
        if (riftKeyParameters != null) {
            var keyValue = riftKeyParameters.getParameter(key);
            if (keyValue != null) {
                return keyValue;
            }
        }
        return config.getValue(tier, rng.fromHashOf(key.location()),
                (key2) -> computeDefaultValueForParameter(registry.getValue(key2), rng, tier, key2, registry,
                        riftKeyParameters));
    }

    public static RiftParameterData forLevel(ServerLevel level) {
        return level.getData(WotrAttachments.RIFT_PARAMETER_DATA);
    }

    public RiftParameterInstance getParameter(ResourceKey<RiftParameter> typeHolder) {
        return parameters.get(typeHolder);
    }

    public void apply(RiftParameterModifier modifier) {
        modifier.applicableParameters().forEach(it -> {
            modifier.enable(getParameter(it.getKey()));
        });
    }

    public void remove(RiftParameterModifier modifier) {
        modifier.applicableParameters().forEach(it -> {
            modifier.disable(getParameter(it.getKey()));
        });
    }
}
