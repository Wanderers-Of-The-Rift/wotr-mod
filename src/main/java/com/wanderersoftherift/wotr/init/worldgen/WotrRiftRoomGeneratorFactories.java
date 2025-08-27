package com.wanderersoftherift.wotr.init.worldgen;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.world.level.levelgen.roomgen.CachedRiftRoomGenerator;
import com.wanderersoftherift.wotr.world.level.levelgen.roomgen.CoreRiftRoomGenerator;
import com.wanderersoftherift.wotr.world.level.levelgen.roomgen.LayerGeneratableRiftRoomGenerator;
import com.wanderersoftherift.wotr.world.level.levelgen.roomgen.RiftRoomGenerator;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class WotrRiftRoomGeneratorFactories {
    public static final DeferredRegister<MapCodec<? extends RiftRoomGenerator.Factory>> RIFT_ROOM_GENERATOR_FACTORIES = DeferredRegister
            .create(WotrRegistries.RIFT_ROOM_GENERATOR_FACTORY_TYPES, WanderersOfTheRift.MODID);

    public static final Supplier<MapCodec<CachedRiftRoomGenerator.Factory>> CACHED_RIFT_ROOM_GENERATOR = RIFT_ROOM_GENERATOR_FACTORIES
            .register("cached", () -> CachedRiftRoomGenerator.Factory.CODEC);

    public static final Supplier<MapCodec<LayerGeneratableRiftRoomGenerator.Factory>> LAYER_GENERATABLE_RIFT_ROOM_GENERATOR = RIFT_ROOM_GENERATOR_FACTORIES
            .register("layer_generatable", () -> LayerGeneratableRiftRoomGenerator.Factory.CODEC);

    public static final Supplier<MapCodec<CoreRiftRoomGenerator.Factory>> CORE_RIFT_ROOM_GENERATOR = RIFT_ROOM_GENERATOR_FACTORIES
            .register("core", () -> CoreRiftRoomGenerator.Factory.CODEC);

}