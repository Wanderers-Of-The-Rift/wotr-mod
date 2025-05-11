package com.wanderersoftherift.wotr.item.riftkey;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.ModRiftThemes;
import com.wanderersoftherift.wotr.init.RegistryEvents;
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
import java.util.List;
import java.util.Optional;

/**
 * Configuration for generating a rift
 *
 * @param tier      The tier of the rift
 * @param theme     The theme of the rift
 * @param objective The objective of the rift
 * @param seed      Optional, the seed for the rift
 */
public record RiftConfig(int tier, Optional<Holder<RiftTheme>> theme, Optional<Holder<ObjectiveType>> objective,
        Optional<Integer> seed) {

    public static final Codec<RiftConfig> CODEC = RecordCodecBuilder
            .create(instance -> instance
                    .group(Codec.INT.fieldOf("tier").forGetter(RiftConfig::tier),
                            RiftTheme.CODEC.optionalFieldOf("theme").forGetter(RiftConfig::theme),
                            ObjectiveType.CODEC.optionalFieldOf("objective").forGetter(RiftConfig::objective),
                            Codec.INT.optionalFieldOf("seed").forGetter(RiftConfig::seed))
                    .apply(instance, RiftConfig::new));

    // spotless:off
    public static final StreamCodec<RegistryFriendlyByteBuf, RiftConfig> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, RiftConfig::tier,
            ByteBufCodecs.holderRegistry(ModRiftThemes.RIFT_THEME_KEY).apply(ByteBufCodecs::optional), RiftConfig::theme,
            ByteBufCodecs.holderRegistry(RegistryEvents.OBJECTIVE_REGISTRY).apply(ByteBufCodecs::optional), RiftConfig::objective,
            ByteBufCodecs.INT.apply(ByteBufCodecs::optional), RiftConfig::seed,
            RiftConfig::new);
    // spotless:on

    public RiftConfig(int tier) {
        this(tier, Optional.empty(), Optional.empty(), Optional.empty());
    }

    public RiftConfig(int tier, Holder<RiftTheme> theme) {
        this(tier, Optional.of(theme), Optional.empty(), Optional.empty());
    }

    public RiftConfig(int tier, Holder<RiftTheme> theme, int seed) {
        this(tier, Optional.of(theme), Optional.empty(), Optional.of(seed));
    }

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
        seed.ifPresent(seed -> {
            result.add(Component.translatable(WanderersOfTheRift.translationId("tooltip", "rift_key_seed"), seed)
                    .withColor(ChatFormatting.GRAY.getColor()));
        });
        return result;
    }

    public Builder modify() {
        return new Builder(this);
    }

    public static class Builder {
        private int tier;
        private Optional<Holder<RiftTheme>> theme;
        private Optional<Holder<ObjectiveType>> objective;
        private Optional<Integer> seed;

        public Builder() {
            this.tier = 0;
            this.theme = Optional.empty();
            this.objective = Optional.empty();
            this.seed = Optional.empty();
        }

        private Builder(RiftConfig source) {
            this.tier = source.tier;
            this.theme = source.theme;
            this.objective = source.objective;
            this.seed = source.seed;
        }

        public Builder tier(int tier) {
            this.tier = tier;
            return this;
        }

        public Builder theme(Holder<RiftTheme> theme) {
            this.theme = Optional.of(theme);
            return this;
        }

        public Builder objective(Holder<ObjectiveType> objective) {
            this.objective = Optional.of(objective);
            return this;
        }

        public Builder seed(int seed) {
            this.seed = Optional.of(seed);
            return this;
        }

        public RiftConfig build() {
            return new RiftConfig(tier, theme, objective, seed);
        }
    }
}
