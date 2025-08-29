package com.wanderersoftherift.wotr.core.rift;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.rift.objective.ObjectiveType;
import com.wanderersoftherift.wotr.world.level.levelgen.theme.RiftTheme;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Map;

/**
 * Configuration for generating a rift
 *
 * @param tier       The tier of the rift
 * @param theme      The theme of the rift
 * @param objective  The objective of the rift
 * @param customData Additional data
 */

public record RiftConfig(int tier, Holder<RiftTheme> theme, Holder<ObjectiveType> objective, long seed,
        Map<Holder<RiftConfigData.RiftConfigDataType<?>>, RiftConfigData> customData) {

    public static final Codec<RiftConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("tier").forGetter(RiftConfig::tier),
            RiftTheme.CODEC.fieldOf("theme").forGetter(RiftConfig::theme),
            ObjectiveType.CODEC.fieldOf("objective").forGetter(RiftConfig::objective),
            Codec.LONG.fieldOf("seed").forGetter(RiftConfig::seed),
            RiftConfigData.DISPATCHED_MAP_CODEC.optionalFieldOf("custom_data", ImmutableMap.of())
                    .forGetter(RiftConfig::customData)
    ).apply(instance, RiftConfig::new));

    // spotless:off
    public static final StreamCodec<RegistryFriendlyByteBuf, RiftConfig> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.INT, RiftConfig::tier,
                ByteBufCodecs.holderRegistry(WotrRegistries.Keys.RIFT_THEMES), RiftConfig::theme,
                ByteBufCodecs.holderRegistry(WotrRegistries.Keys.OBJECTIVES), RiftConfig::objective,
                ByteBufCodecs.LONG, RiftConfig::seed,
                RiftConfigData.DISPATCHED_MAP_STREAM_CODEC, RiftConfig::customData,
            RiftConfig::new);
    // spotless:on

    public RiftConfig withObjective(Holder<ObjectiveType> objective) {
        return new RiftConfig(tier, theme, objective, seed, customData);
    }

    public RiftConfig withTheme(Holder<RiftTheme> theme) {
        return new RiftConfig(tier, theme, objective, seed, customData);
    }

    public RiftConfig withTier(int tier) {
        return new RiftConfig(tier, theme, objective, seed, customData);
    }

    public RiftConfig withSeed(int seed) {
        return new RiftConfig(tier, theme, objective, seed, customData);
    }

    public <T extends RiftConfigData> RiftConfig withCustomData(
            DeferredHolder<RiftConfigData.RiftConfigDataType<?>, RiftConfigData.RiftConfigDataType<T>> typeHolder,
            T newValue) {
        var newDataMap = ImmutableMap.<Holder<RiftConfigData.RiftConfigDataType<?>>, RiftConfigData>builder();
        newDataMap.putAll(customData);
        newDataMap.put(typeHolder.getDelegate(), newValue);

        return new RiftConfig(tier, theme, objective, seed, newDataMap.build());
    }

    public <T extends RiftConfigData> T getCustomData(
            DeferredHolder<RiftConfigData.RiftConfigDataType<?>, RiftConfigData.RiftConfigDataType<T>> typeHolder) {
        return (T) customData.get(typeHolder.getDelegate());
    }
}
