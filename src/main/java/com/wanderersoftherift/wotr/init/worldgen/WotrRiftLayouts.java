package com.wanderersoftherift.wotr.init.worldgen;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.LayeredFiniteRiftLayout;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.LayeredInfiniteRiftLayout;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.RiftLayout;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class WotrRiftLayouts {
    public static final DeferredRegister<MapCodec<? extends RiftLayout.Factory>> LAYOUTS = DeferredRegister
            .create(WotrRegistries.LAYOUT_TYPES, WanderersOfTheRift.MODID);

    public static final Supplier<MapCodec<LayeredFiniteRiftLayout.Factory>> FINITE_LAYERED_LAYOUT = LAYOUTS
            .register("layered_layout", () -> LayeredFiniteRiftLayout.Factory.CODEC);
    public static final Supplier<MapCodec<LayeredInfiniteRiftLayout.Factory>> INFINITE_LAYERED_LAYOUT = LAYOUTS
            .register("infinite_layered_layout", () -> LayeredInfiniteRiftLayout.Factory.CODEC);

}
