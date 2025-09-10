package com.wanderersoftherift.wotr.core.rift;

import com.google.common.collect.ImmutableMap;
import com.wanderersoftherift.wotr.init.WotrDataComponentType;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.init.WotrTags;
import com.wanderersoftherift.wotr.rift.objective.ObjectiveType;
import com.wanderersoftherift.wotr.util.RandomSourceFromJavaRandom;
import com.wanderersoftherift.wotr.world.level.levelgen.theme.RiftTheme;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

public final class RiftConfigInitialization {

    public static RiftConfig initializeConfig(ItemStack item, RegistryAccess registries) {
        var random = RandomSource.create();
        var seedOptional = item.get(WotrDataComponentType.RiftKeyData.RIFT_SEED);
        var themeOptional = item.get(WotrDataComponentType.RiftKeyData.RIFT_THEME);
        var tierOptional = item.get(WotrDataComponentType.RiftKeyData.RIFT_TIER);
        var objectiveOptional = item.get(WotrDataComponentType.RiftKeyData.RIFT_OBJECTIVE);
        long seed = Objects.requireNonNullElseGet(seedOptional, random::nextLong);
        var objective = Objects.requireNonNullElseGet(objectiveOptional, () -> defaultObjective(registries, seed));
        var theme = Objects.requireNonNullElseGet(themeOptional, () -> getRandomTheme(registries, seed));
        var tier = Objects.requireNonNullElse(tierOptional, 0);

        var dataMap = ImmutableMap.<Holder<RiftConfigDataType<?>>, Object>builder();
        var dataTypeRegistry = registries.lookupOrThrow(WotrRegistries.Keys.RIFT_CONFIG_DATA_TYPES);
        dataTypeRegistry.forEach(type -> {
            var value = type.initialize(item, seed, registries);
            var typeHolder = dataTypeRegistry.wrapAsHolder(type);
            dataMap.put(typeHolder, value);
        });

        return new RiftConfig(
                tier, theme, objective, seed, dataMap.build()
        );
    }

    private static Holder<ObjectiveType> defaultObjective(RegistryAccess registries, long seed) {
        return registries.lookupOrThrow(WotrRegistries.Keys.OBJECTIVES)
                .getRandomElementOf(WotrTags.Objectives.RANDOM_SELECTABLE, RandomSource.create(seed + 668_453_148))
                .orElseThrow(() -> new IllegalStateException("No objectives available"));
    }

    private static Holder<RiftTheme> getRandomTheme(RegistryAccess registries, long seed) {

        var themeRandom = new RandomSourceFromJavaRandom(RandomSourceFromJavaRandom.get(0),
                seed * 5624397638181617163L);

        Registry<RiftTheme> registry = registries.lookupOrThrow(WotrRegistries.Keys.RIFT_THEMES);

        return registry.getRandomElementOf(WotrTags.RiftThemes.RANDOM_SELECTABLE, themeRandom)
                .orElseThrow(() -> new IllegalStateException("No rift themes available"));
    }

}
