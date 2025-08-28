package com.wanderersoftherift.wotr.core.rift;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.rift.objective.ObjectiveType;
import com.wanderersoftherift.wotr.world.level.levelgen.theme.RiftTheme;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration for generating a rift
 *
 * @param tier      The tier of the rift
 * @param theme     The theme of the rift
 * @param objective The objective of the rift
 * @param riftGen   Additional generation config
 */

public record RiftConfig(int tier, Holder<RiftTheme> theme, Holder<ObjectiveType> objective,
        RiftGenerationConfig riftGen,
        Map<Holder<MapCodec<? extends RiftConfigCustomData>>, RiftConfigCustomData> customData) {

    public static final Codec<RiftConfig> CODEC = RecordCodecBuilder
            .create(instance -> instance.group(Codec.INT.fieldOf("tier").forGetter(RiftConfig::tier),
                    RiftTheme.CODEC.fieldOf("theme").forGetter(RiftConfig::theme),
                    ObjectiveType.CODEC.fieldOf("objective").forGetter(RiftConfig::objective),
                    RiftGenerationConfig.CODEC.optionalFieldOf("rift_gen", RiftGenerationConfig.EMPTY)
                            .forGetter(RiftConfig::riftGen),
                    RiftConfigCustomData.DISPATCHED_MAP_CODEC.optionalFieldOf("custom_data", new HashMap<>())
                            .forGetter(RiftConfig::customData)
            ).apply(instance, RiftConfig::new));

    // spotless:off
    public static final StreamCodec<RegistryFriendlyByteBuf, RiftConfig> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.INT, RiftConfig::tier,
                ByteBufCodecs.holderRegistry(WotrRegistries.Keys.RIFT_THEMES), RiftConfig::theme,
                ByteBufCodecs.holderRegistry(WotrRegistries.Keys.OBJECTIVES), RiftConfig::objective,
                RiftGenerationConfig.STREAM_CODEC, RiftConfig::riftGen,
                RiftConfigCustomData.DISPATCHED_MAP_STREAM_CODEC, RiftConfig::customData,
            RiftConfig::new);
    // spotless:on

    public RiftConfig withRiftGenerationConfig(RiftGenerationConfig riftGen) {
        return new RiftConfig(tier, theme, objective, riftGen, customData);
    }

    public RiftConfig withObjective(Holder<ObjectiveType> objective) {
        return new RiftConfig(tier, theme, objective, riftGen, customData);
    }

    public RiftConfig withTheme(Holder<RiftTheme> theme) {
        return new RiftConfig(tier, theme, objective, riftGen, customData);
    }

    public RiftConfig withTier(int tier) {
        return new RiftConfig(tier, theme, objective, riftGen, customData);
    }

    public <T extends RiftConfigCustomData> T getCustomData(Holder<MapCodec<T>> key) {
        return (T) customData.get(key);
    }

    public <T extends RiftConfigCustomData> T putCustomData(Holder<MapCodec<T>> key, T newValue) {
        return (T) customData.put((Holder<MapCodec<? extends RiftConfigCustomData>>) (Object) key, newValue);
    }

}
