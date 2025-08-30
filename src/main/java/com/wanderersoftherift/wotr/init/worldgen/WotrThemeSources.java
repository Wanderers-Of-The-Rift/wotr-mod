package com.wanderersoftherift.wotr.init.worldgen;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.theme.FixedThemeSource;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.theme.ThemeSource;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.theme.ThemeSourceFromRiftData;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class WotrThemeSources {
    public static final DeferredRegister<MapCodec<? extends ThemeSource>> THEME_SOURCES = DeferredRegister
            .create(WotrRegistries.THEME_SOURCE_TYPE, WanderersOfTheRift.MODID);

    public static final Supplier<MapCodec<FixedThemeSource>> FIXED_THEME_SOURCE = THEME_SOURCES.register("fixed",
            () -> FixedThemeSource.CODEC);
    public static final Supplier<MapCodec<ThemeSourceFromRiftData>> RIFT_DATA_THEME_SOURCE = THEME_SOURCES
            .register("rift_data", () -> ThemeSourceFromRiftData.CODEC);
}
