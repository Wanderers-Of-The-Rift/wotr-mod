package com.wanderersoftherift.wotr.spawning.functions;

import com.mojang.serialization.MapCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;

public record MoveToSpawnerSpawnFunction() implements SpawnFunction {

    public static final MoveToSpawnerSpawnFunction INSTANCE = new MoveToSpawnerSpawnFunction();
    public static final MapCodec<MoveToSpawnerSpawnFunction> MAP_CODEC = MapCodec.unit(INSTANCE);

    @Override
    public MapCodec<? extends SpawnFunction> codec() {
        return MAP_CODEC;
    }

    @Override
    public void applyToMob(Entity entity, BlockEntity spawner, RandomSource random) {
        entity.moveTo(spawner.getBlockPos(), 0, 0);
    }
}
