package com.wanderersoftherift.wotr.loot.provider.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.rift.parameter.RiftParameterData;
import com.wanderersoftherift.wotr.core.rift.parameter.RiftParameterInstance;
import com.wanderersoftherift.wotr.core.rift.parameter.definitions.RiftParameter;
import com.wanderersoftherift.wotr.init.WotrNumberProviders;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.init.loot.WotrLootContextParams;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import org.jetbrains.annotations.NotNull;

/**
 * Provides the value of a specific RiftParameter
 * 
 * @param parameter
 */
public record RiftParameterNumberProvider(ResourceKey<RiftParameter> parameter) implements NumberProvider {

    public static final MapCodec<RiftParameterNumberProvider> CODEC = RecordCodecBuilder
            .mapCodec(instance -> instance.group(
                    ResourceKey.codec(WotrRegistries.Keys.RIFT_PARAMETER_CONFIGS)
                            .fieldOf("parameter")
                            .forGetter(RiftParameterNumberProvider::parameter)
            ).apply(instance, RiftParameterNumberProvider::new));

    @Override
    public float getFloat(@NotNull LootContext lootContext) {
        RiftParameterData riftParams = lootContext.getParameter(WotrLootContextParams.RIFT_PARAMETERS);
        RiftParameterInstance paramInstance = riftParams.getParameter(parameter);
        if (paramInstance != null) {
            return (float) paramInstance.get();
        } else {
            WanderersOfTheRift.LOGGER.info("Unset rift parameter '{}'", parameter.location());
            return 0;
        }
    }

    @Override
    public @NotNull LootNumberProviderType getType() {
        return WotrNumberProviders.RIFT_PARAMETER.get();
    }
}
