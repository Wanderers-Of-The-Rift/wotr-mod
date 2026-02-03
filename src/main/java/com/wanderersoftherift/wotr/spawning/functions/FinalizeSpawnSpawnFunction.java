package com.wanderersoftherift.wotr.spawning.functions;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.event.EventHooks;

public record FinalizeSpawnSpawnFunction() implements SpawnFunction {
    public static final FinalizeSpawnSpawnFunction INSTANCE = new FinalizeSpawnSpawnFunction();
    public static final MapCodec<FinalizeSpawnSpawnFunction> MAP_CODEC = MapCodec.unit(INSTANCE);

    @Override
    public MapCodec<? extends SpawnFunction> codec() {
        return MAP_CODEC;
    }

    @Override
    public void applyToMob(Entity entity, BlockEntity spawner, RandomSource random) {
        if (!(spawner.getLevel() instanceof ServerLevel serverLevel && entity instanceof Mob mob)) {
            return;
        }
        EventHooks.finalizeMobSpawnSpawner(mob, serverLevel, serverLevel.getCurrentDifficultyAt(entity.blockPosition()),
                EntitySpawnReason.SPAWNER, null, () -> Either.left(spawner), false);
    }
}
