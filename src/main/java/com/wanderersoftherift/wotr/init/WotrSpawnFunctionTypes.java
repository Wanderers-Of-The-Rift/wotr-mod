package com.wanderersoftherift.wotr.init;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.spawning.functions.AddDeathNotifierSpawnFunction;
import com.wanderersoftherift.wotr.spawning.functions.AddPassengerSpawnFunction;
import com.wanderersoftherift.wotr.spawning.functions.ApplyMobVariantSpawnFunction;
import com.wanderersoftherift.wotr.spawning.functions.ChanceSpawnFunction;
import com.wanderersoftherift.wotr.spawning.functions.EquipmentSpawnFunction;
import com.wanderersoftherift.wotr.spawning.functions.FinalizeSpawnSpawnFunction;
import com.wanderersoftherift.wotr.spawning.functions.ListSpawnFunction;
import com.wanderersoftherift.wotr.spawning.functions.MoveSpawnFunction;
import com.wanderersoftherift.wotr.spawning.functions.MoveToSpawnerSpawnFunction;
import com.wanderersoftherift.wotr.spawning.functions.SpawnFunction;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class WotrSpawnFunctionTypes {
    public static final DeferredRegister<MapCodec<? extends SpawnFunction>> SPAWN_FUNCTION_TYPES = DeferredRegister
            .create(WotrRegistries.SPAWN_FUNCTION_TYPES, WanderersOfTheRift.MODID);

    public static final Supplier<MapCodec<? extends SpawnFunction>> APPLY_MOB_VARIANT = SPAWN_FUNCTION_TYPES
            .register("apply_mob_variant", () -> ApplyMobVariantSpawnFunction.MAP_CODEC);
    public static final Supplier<MapCodec<? extends SpawnFunction>> ADD_DEATH_NOTIFIER = SPAWN_FUNCTION_TYPES
            .register("add_death_notifier", () -> AddDeathNotifierSpawnFunction.MAP_CODEC);
    public static final Supplier<MapCodec<? extends SpawnFunction>> FINALIZE_SPAWN = SPAWN_FUNCTION_TYPES
            .register("finalize_spawn", () -> FinalizeSpawnSpawnFunction.MAP_CODEC);
    public static final Supplier<MapCodec<? extends SpawnFunction>> LIST = SPAWN_FUNCTION_TYPES.register("list",
            () -> ListSpawnFunction.MAP_CODEC);
    public static final Supplier<MapCodec<? extends SpawnFunction>> CHANCE = SPAWN_FUNCTION_TYPES.register("chance",
            () -> ChanceSpawnFunction.MAP_CODEC);
    public static final Supplier<MapCodec<? extends SpawnFunction>> EQUIPMENT = SPAWN_FUNCTION_TYPES.register("equip",
            () -> EquipmentSpawnFunction.MAP_CODEC);

    public static final Supplier<MapCodec<? extends SpawnFunction>> ADD_PASSENGER = SPAWN_FUNCTION_TYPES
            .register("add_passenger", () -> AddPassengerSpawnFunction.MAP_CODEC);

    public static final Supplier<MapCodec<? extends SpawnFunction>> MOVE_TO_SPAWNER = SPAWN_FUNCTION_TYPES
            .register("move_to_spawner", () -> MoveToSpawnerSpawnFunction.MAP_CODEC);
    public static final Supplier<MapCodec<? extends SpawnFunction>> MOVE = SPAWN_FUNCTION_TYPES.register("move",
            () -> MoveSpawnFunction.MAP_CODEC);
    /*
     * public static final Supplier<MapCodec<? extends SpawnFunction>> FIND_SPAWN_LOCATION = SPAWN_FUNCTION_TYPES
     * .register("find_spawn_location", () -> FindSuitableSpawnLocationSpawnFunction.MAP_CODEC);
     */
}
