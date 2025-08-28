package com.wanderersoftherift.wotr.core.rift;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.WotrDataComponentType;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.init.WotrTags;
import com.wanderersoftherift.wotr.rift.objective.ObjectiveType;
import com.wanderersoftherift.wotr.util.RandomSourceFromJavaRandom;
import com.wanderersoftherift.wotr.world.level.levelgen.theme.RiftTheme;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Objects;

final class RiftConfigInitialization {

    private static final ResourceKey<RiftGenerationConfig> DEFAULT_PRESET_KEY = ResourceKey
            .create(WotrRegistries.Keys.GENERATOR_PRESETS, WanderersOfTheRift.id("default"));

    static RiftConfig initializeConfig(ItemStack item, MinecraftServer server) {
        var random = RandomSource.create();
        var seedOptional = item.get(WotrDataComponentType.RiftConfig.RIFT_SEED);
        var themeOptional = item.get(WotrDataComponentType.RiftConfig.RIFT_THEME);
        var tierOptional = item.get(WotrDataComponentType.RiftConfig.ITEM_RIFT_TIER);
        var objectiveOptional = item.get(WotrDataComponentType.RiftConfig.RIFT_OBJECTIVE);
        long seed = Objects.requireNonNullElseGet(seedOptional, random::nextLong);
        var objective = Objects.requireNonNullElseGet(objectiveOptional, () -> defaultObjective(server, seed));
        var theme = Objects.requireNonNullElseGet(themeOptional, () -> getRandomTheme(server, seed));
        var tier = Objects.requireNonNullElse(tierOptional, 0);
        return new RiftConfig(
                tier, theme, objective, riftGen(item, server), seed, new HashMap<>()
        );
    }

    private static RiftGenerationConfig riftGen(ItemStack item, MinecraftServer server) {
        var preset = item.get(WotrDataComponentType.RiftConfig.GENERATOR_PRESET);
        if (preset == null) {
            preset = server.registryAccess().holderOrThrow(DEFAULT_PRESET_KEY);
        }
        var config = preset.value();
        // todo overrides
        return config;
    }

    private static Holder<ObjectiveType> defaultObjective(MinecraftServer server, long seed) {
        return server.registryAccess()
                .lookupOrThrow(WotrRegistries.Keys.OBJECTIVES)
                .getRandomElementOf(WotrTags.Objectives.RANDOM_SELECTABLE, RandomSource.create(seed + 668_453_148))
                .orElseThrow(() -> new IllegalStateException("No objectives available"));
    }

    private static Holder<RiftTheme> getRandomTheme(MinecraftServer server, long seed) {

        var themeRandom = new RandomSourceFromJavaRandom(RandomSourceFromJavaRandom.get(0),
                seed * 5624397638181617163L);

        Registry<RiftTheme> registry = server.registryAccess().lookupOrThrow(WotrRegistries.Keys.RIFT_THEMES);

        return registry.getRandomElementOf(WotrTags.RiftThemes.RANDOM_SELECTABLE, themeRandom)
                .orElseThrow(() -> new IllegalStateException("No rift themes available"));
    }

}
