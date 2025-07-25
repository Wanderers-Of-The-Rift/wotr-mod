package com.wanderersoftherift.wotr.item.riftkey;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.rift.objective.ObjectiveType;
import com.wanderersoftherift.wotr.world.level.levelgen.jigsaw.JigsawListProcessor;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.RiftLayout;
import com.wanderersoftherift.wotr.world.level.levelgen.roomgen.RiftRoomGenerator;
import com.wanderersoftherift.wotr.world.level.levelgen.theme.RiftTheme;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Collections;
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
// TODO: Move into core.rift
public record RiftConfig(int tier, Optional<Holder<RiftTheme>> theme, Optional<Holder<ObjectiveType>> objective,
        Optional<RiftLayout.Factory> layout, Optional<RiftRoomGenerator.Factory> roomGenerator,
        boolean generatePassages, List<JigsawListProcessor> jigsawProcessors, Optional<Integer> seed) {

    public static final Codec<RiftConfig> CODEC = RecordCodecBuilder
            .create(instance -> instance
                    .group(Codec.INT.fieldOf("tier").forGetter(RiftConfig::tier),
                            RiftTheme.CODEC.optionalFieldOf("theme").forGetter(RiftConfig::theme),
                            ObjectiveType.CODEC.optionalFieldOf("objective").forGetter(RiftConfig::objective),
                            RiftLayout.Factory.CODEC.optionalFieldOf("layout").forGetter(RiftConfig::layout),
                            RiftRoomGenerator.Factory.CODEC.optionalFieldOf("room_generator")
                                    .forGetter(RiftConfig::roomGenerator),
                            Codec.BOOL.optionalFieldOf("passages", true).forGetter(RiftConfig::generatePassages),
                            JigsawListProcessor.CODEC.listOf()
                                    .optionalFieldOf("jigsaw_processors", Collections.emptyList())
                                    .forGetter(RiftConfig::jigsawProcessors),
                            Codec.INT.optionalFieldOf("seed").forGetter(RiftConfig::seed))
                    .apply(instance, RiftConfig::new));

    // spotless:off
    public static final StreamCodec<RegistryFriendlyByteBuf, RiftConfig> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, RiftConfig::tier,
            ByteBufCodecs.holderRegistry(WotrRegistries.Keys.RIFT_THEMES).apply(ByteBufCodecs::optional), RiftConfig::theme,
            ByteBufCodecs.holderRegistry(WotrRegistries.Keys.OBJECTIVES).apply(ByteBufCodecs::optional), RiftConfig::objective,
            ByteBufCodecs.fromCodec(RiftLayout.Factory.CODEC).apply(ByteBufCodecs::optional), RiftConfig::layout,
            ByteBufCodecs.fromCodec(RiftRoomGenerator.Factory.CODEC).apply(ByteBufCodecs::optional), RiftConfig::roomGenerator,
            ByteBufCodecs.BOOL.apply(ByteBufCodecs::optional).map(it->it.orElse(true), Optional::of), RiftConfig::generatePassages,
            ByteBufCodecs.fromCodec(JigsawListProcessor.CODEC).apply(ByteBufCodecs.list()), RiftConfig::jigsawProcessors,
            ByteBufCodecs.INT.apply(ByteBufCodecs::optional), RiftConfig::seed,
            RiftConfig::new);
    // spotless:on

    public RiftConfig(int tier) {
        this(tier, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), true,
                Collections.emptyList(), Optional.empty());
    }

    public RiftConfig(int tier, Holder<RiftTheme> theme) {
        this(tier, Optional.of(theme), Optional.empty(), Optional.empty(), Optional.empty(), true,
                Collections.emptyList(), Optional.empty());
    }

    public RiftConfig(int tier, Holder<RiftTheme> theme, int seed) {
        this(tier, Optional.of(theme), Optional.empty(), Optional.empty(), Optional.empty(), true,
                Collections.emptyList(), Optional.of(seed));
    }

    public RiftConfig withSeedIfAbsent(int seed) {
        if (this.seed.isPresent()) {
            return this;
        } else {
            return withSeed(seed);
        }
    }

    public RiftConfig withLayoutIfAbsent(RiftLayout.Factory layout) {
        if (this.layout.isPresent()) {
            return this;
        } else {
            return withLayout(layout);
        }
    }

    public RiftConfig withRoomGeneratorIfAbsent(RiftRoomGenerator.Factory generator) {
        if (this.roomGenerator.isPresent()) {
            return this;
        } else {
            return withRoomGenerator(generator);
        }
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

    public RiftConfig withSeed(int seed) {
        return new RiftConfig(tier, theme, objective, layout, roomGenerator, generatePassages, jigsawProcessors,
                Optional.of(seed));
    }

    public RiftConfig withLayout(RiftLayout.Factory layout) {
        return new RiftConfig(tier, theme, objective, Optional.of(layout), roomGenerator, generatePassages,
                jigsawProcessors, seed);
    }

    public RiftConfig withJigsawProcessors(List<JigsawListProcessor> processors) {
        return new RiftConfig(tier, theme, objective, layout, roomGenerator, generatePassages, processors, seed);
    }

    public RiftConfig withRoomGenerator(RiftRoomGenerator.Factory roomGenerator) {
        return new RiftConfig(tier, theme, objective, layout, Optional.of(roomGenerator), generatePassages,
                jigsawProcessors, seed);
    }

    public RiftConfig withObjective(Holder<ObjectiveType> objective) {
        return new RiftConfig(tier, theme, Optional.of(objective), layout, roomGenerator, generatePassages,
                jigsawProcessors, seed);
    }

    public RiftConfig withTheme(Holder<RiftTheme> theme) {
        return new RiftConfig(tier, Optional.of(theme), objective, layout, roomGenerator, generatePassages,
                jigsawProcessors, seed);
    }

    public RiftConfig withTier(int tier) {
        return new RiftConfig(tier, theme, objective, layout, roomGenerator, generatePassages, jigsawProcessors, seed);
    }

    public RiftConfig withPassages() {
        return new RiftConfig(tier, theme, objective, layout, roomGenerator, true, jigsawProcessors, seed);
    }

    public RiftConfig withoutPassages() {
        return new RiftConfig(tier, theme, objective, layout, roomGenerator, false, jigsawProcessors, seed);
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
        seed.ifPresent(seed -> {
            result.add(Component.translatable(WanderersOfTheRift.translationId("tooltip", "rift_key_seed"), seed)
                    .withColor(ChatFormatting.GRAY.getColor()));
        });
        return result;
    }
}
