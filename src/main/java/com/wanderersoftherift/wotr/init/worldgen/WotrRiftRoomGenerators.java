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

public class WotrRiftRoomGenerators {
    public static final DeferredRegister<MapCodec<? extends RiftRoomGenerator>> RIFT_ROOM_GENERATORS = DeferredRegister
            .create(WotrRegistries.RIFT_ROOM_GENERATOR_TYPES, WanderersOfTheRift.MODID);

    public static final Supplier<MapCodec<CachedRiftRoomGenerator>> CACHED_RIFT_ROOM_GENERATOR = RIFT_ROOM_GENERATORS
            .register("cached", () -> CachedRiftRoomGenerator.CODEC);

    public static final Supplier<MapCodec<LayerGeneratableRiftRoomGenerator>> LAYER_GENERATABLE_RIFT_ROOM_GENERATOR = RIFT_ROOM_GENERATORS
            .register("layer_generatable", () -> LayerGeneratableRiftRoomGenerator.CODEC);

    public static final Supplier<MapCodec<CoreRiftRoomGenerator>> CORE_RIFT_ROOM_GENERATOR = RIFT_ROOM_GENERATORS
            .register("core", () -> CoreRiftRoomGenerator.CODEC);

}
