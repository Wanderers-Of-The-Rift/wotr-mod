package com.wanderersoftherift.wotr.item.riftkey;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.rift.objective.ObjectiveType;
import com.wanderersoftherift.wotr.world.level.levelgen.theme.RiftTheme;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Configuration for generating a rift
 *
 * @param tier      The tier of the rift
 * @param theme     The theme of the rift
 * @param objective The objective of the rift
 * @param riftGen   Additional generation config
 */
// TODO: Move into core.rift
public record RiftConfig(int tier, Optional<Holder<RiftTheme>> theme, Optional<Holder<ObjectiveType>> objective,
        RiftGenerationConfig riftGen,
        Map<Holder<MapCodec<? extends RiftConfigCustomData>>, RiftConfigCustomData> customData) {

    public static final Codec<RiftConfig> CODEC = RecordCodecBuilder
            .create(instance -> instance.group(Codec.INT.fieldOf("tier").forGetter(RiftConfig::tier),
                    RiftTheme.CODEC.optionalFieldOf("theme").forGetter(RiftConfig::theme),
                    ObjectiveType.CODEC.optionalFieldOf("objective").forGetter(RiftConfig::objective),
                    RiftGenerationConfig.CODEC.optionalFieldOf("rift_gen", RiftGenerationConfig.EMPTY)
                            .forGetter(RiftConfig::riftGen),
                    RiftConfigCustomData.DISPATCHED_MAP_CODEC.optionalFieldOf("custom_data", new HashMap<>())
                            .forGetter(RiftConfig::customData)
            ).apply(instance, RiftConfig::new));

    // spotless:off
    public static final StreamCodec<RegistryFriendlyByteBuf, RiftConfig> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.INT, RiftConfig::tier,
                ByteBufCodecs.holderRegistry(WotrRegistries.Keys.RIFT_THEMES).apply(ByteBufCodecs::optional), RiftConfig::theme,
                ByteBufCodecs.holderRegistry(WotrRegistries.Keys.OBJECTIVES).apply(ByteBufCodecs::optional), RiftConfig::objective,
                RiftGenerationConfig.STREAM_CODEC, RiftConfig::riftGen,
                RiftConfigCustomData.DISPATCHED_MAP_STREAM_CODEC, RiftConfig::customData,
            RiftConfig::new);
    // spotless:on

    public RiftConfig(int tier) {
        this(tier, Optional.empty(), Optional.empty(), RiftGenerationConfig.EMPTY, new HashMap<>());
    }

    public RiftConfig(int tier, Holder<RiftTheme> theme) {
        this(tier, Optional.of(theme), Optional.empty(), RiftGenerationConfig.EMPTY, new HashMap<>());
    }

    public RiftConfig(int tier, Holder<RiftTheme> theme, int seed) {
        this(tier, Optional.of(theme), Optional.empty(), RiftGenerationConfig.EMPTY.withSeed(seed), new HashMap<>());
    }

    public RiftConfig withObjectiveIfAbsent(Holder<ObjectiveType> objective) {
        if (this.objective.isPresent()) {
            return this;
        } else {
            return withObjective(objective);
        }
    }

    public RiftConfig withThemeIfAbsent(Holder<RiftTheme> theme) {
        if (this.theme.isPresent()) {
            return this;
        } else {
            return withTheme(theme);
        }
    }

    public RiftConfig withRiftGenerationConfig(RiftGenerationConfig riftGen) {
        return new RiftConfig(tier, theme, objective, riftGen, customData);
    }

    public RiftConfig withObjective(Holder<ObjectiveType> objective) {
        return new RiftConfig(tier, theme, Optional.of(objective), riftGen, customData);
    }

    public RiftConfig withTheme(Holder<RiftTheme> theme) {
        return new RiftConfig(tier, Optional.of(theme), objective, riftGen, customData);
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

    /**
     * @deprecated Rather than having RiftConfig on an item should migrate to using individual data components
     * @return Components to add to the tooltip
     */
    @Deprecated
    public List<Component> getTooltips() {
        List<Component> result = new ArrayList<>();
        result.add(Component.translatable("tooltip." + WanderersOfTheRift.MODID + ".rift_key_tier", tier)
                .withColor(ChatFormatting.GRAY.getColor()));
        theme.ifPresent(x -> {
            ResourceLocation themeId = ResourceLocation.parse(x.getRegisteredName());
            Component themeName = Component
                    .translatable("rift_theme." + themeId.getNamespace() + "." + themeId.getPath());
            result.add(Component.translatable("tooltip." + WanderersOfTheRift.MODID + ".rift_key_theme", themeName)
                    .withColor(ChatFormatting.GRAY.getColor()));
        });
        objective.ifPresent(x -> {
            ResourceLocation objective = ResourceLocation.parse(x.getRegisteredName());
            Component objectiveName = Component
                    .translatable("objective." + objective.getNamespace() + "." + objective.getPath() + ".name");
            result.add(
                    Component.translatable("tooltip." + WanderersOfTheRift.MODID + ".rift_key_objective", objectiveName)
                            .withColor(ChatFormatting.GRAY.getColor()));
        });
        riftGen.seed().ifPresent(seed -> {
            result.add(Component.translatable(WanderersOfTheRift.translationId("tooltip", "rift_key_seed"), seed)
                    .withColor(ChatFormatting.GRAY.getColor()));
        });
        return result;
    }
}
