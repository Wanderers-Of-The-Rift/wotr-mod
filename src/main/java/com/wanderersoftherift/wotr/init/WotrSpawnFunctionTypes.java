package com.wanderersoftherift.wotr.init;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.block.blockentity.anomaly.BattleTask;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class WotrSpawnFunctionTypes {
    public static final DeferredRegister<MapCodec<? extends BattleTask.SpawnFunction>> SPAWN_FUNCTION_TYPES = DeferredRegister
            .create(WotrRegistries.SPAWN_FUNCTION_TYPES, WanderersOfTheRift.MODID);

    public static final Supplier<MapCodec<? extends BattleTask.SpawnFunction>> APPLY_MOB_VARIANT = SPAWN_FUNCTION_TYPES
            .register("apply_mob_variant", () -> BattleTask.ApplyMobVariant.MAP_CODEC);
    public static final Supplier<MapCodec<? extends BattleTask.SpawnFunction>> ADD_DEATH_NOTIFIER = SPAWN_FUNCTION_TYPES
            .register("add_death_notifier", () -> BattleTask.AddDeathNotifier.MAP_CODEC);
    public static final Supplier<MapCodec<? extends BattleTask.SpawnFunction>> FINALIZE_SPAWN = SPAWN_FUNCTION_TYPES
            .register("finalize_spawn", () -> BattleTask.FinalizeSpawn.MAP_CODEC);
}
