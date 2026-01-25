package com.wanderersoftherift.wotr.spawning.functions;

import com.mojang.serialization.MapCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.entity.BlockEntity;

// TODO
public record FindSuitableSpawnLocationSpawnFunction() implements SpawnFunction {
    @Override
    public MapCodec<? extends SpawnFunction> codec() {
        return null;
    }

    @Override
    public void applyToMob(Mob mob, BlockEntity spawner, RandomSource random) {

    }
}
