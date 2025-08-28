package com.wanderersoftherift.wotr.init.worldgen;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.ThemeProcessor;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class WotrThemeSources {
    public static final DeferredRegister<MapCodec<? extends ThemeProcessor.ThemeSource>> THEME_SOURCES = DeferredRegister
            .create(WotrRegistries.THEME_SOURCE_TYPE, WanderersOfTheRift.MODID);

    public static final Supplier<MapCodec<ThemeProcessor.ThemeSource.Fixed>> FIXED_THEME_SOURCE = THEME_SOURCES
            .register("fixed", () -> ThemeProcessor.ThemeSource.Fixed.CODEC);
    public static final Supplier<MapCodec<ThemeProcessor.ThemeSource.RiftData>> RIFT_DATA_THEME_SOURCE = THEME_SOURCES
            .register("rift_data", () -> ThemeProcessor.ThemeSource.RiftData.CODEC);
}
