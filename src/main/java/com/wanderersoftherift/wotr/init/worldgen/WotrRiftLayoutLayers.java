package com.wanderersoftherift.wotr.init.worldgen;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.LayeredRiftLayout;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.layers.BoxedLayer;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.layers.ChaosLayer;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.layers.PredefinedRoomLayer;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.layers.RingLayer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class WotrRiftLayoutLayers {
    public static final DeferredRegister<MapCodec<? extends LayeredRiftLayout.LayoutLayer.Factory>> LAYOUT_LAYERS = DeferredRegister
            .create(WotrRegistries.LAYOUT_LAYER_TYPES, WanderersOfTheRift.MODID);

    public static final DeferredHolder<MapCodec<? extends LayeredRiftLayout.LayoutLayer.Factory>, MapCodec<BoxedLayer.Factory>> BOXED_LAYER = LAYOUT_LAYERS
            .register("boxed_layer", () -> BoxedLayer.Factory.CODEC);
    public static final DeferredHolder<MapCodec<? extends LayeredRiftLayout.LayoutLayer.Factory>, MapCodec<ChaosLayer.Factory>> CHAOS_LAYER = LAYOUT_LAYERS
            .register("chaos_layer", () -> ChaosLayer.Factory.CODEC);
    public static final DeferredHolder<MapCodec<? extends LayeredRiftLayout.LayoutLayer.Factory>, MapCodec<PredefinedRoomLayer.Factory>> PREDEFINED_LAYER = LAYOUT_LAYERS
            .register("predefined_room_layer", () -> PredefinedRoomLayer.Factory.CODEC);
    public static final DeferredHolder<MapCodec<? extends LayeredRiftLayout.LayoutLayer.Factory>, MapCodec<RingLayer.Factory>> RING_LAYER = LAYOUT_LAYERS
            .register("ring_layer", () -> RingLayer.Factory.CODEC);

}
