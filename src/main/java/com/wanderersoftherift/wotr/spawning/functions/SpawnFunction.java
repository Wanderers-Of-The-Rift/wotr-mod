package com.wanderersoftherift.wotr.spawning.functions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.function.Function;

public interface SpawnFunction {
    Codec<SpawnFunction> CODEC = WotrRegistries.SPAWN_FUNCTION_TYPES.byNameCodec()
            .dispatch(SpawnFunction::codec, Function.identity());

    MapCodec<? extends SpawnFunction> codec();

    void applyToMob(Mob mob, BlockEntity spawner, RandomSource random);
}
