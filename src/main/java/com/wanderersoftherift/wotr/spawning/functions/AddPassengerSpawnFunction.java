package com.wanderersoftherift.wotr.spawning.functions;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.spawning.SpawnType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;

public record AddPassengerSpawnFunction(SpawnType spawn) implements SpawnFunction {
    public static final MapCodec<AddPassengerSpawnFunction> MAP_CODEC = SpawnType.CODEC.fieldOf("spawn")
            .xmap(AddPassengerSpawnFunction::new, AddPassengerSpawnFunction::spawn);

    @Override
    public MapCodec<? extends SpawnFunction> codec() {
        return MAP_CODEC;
    }

    @Override
    public void applyToMob(Entity entity, BlockEntity spawner, RandomSource random) {
        var spawn = this.spawn.createSpawn((ServerLevel) entity.level(), spawner, random);
        if (spawn != null) {
            spawn.startRiding(entity, true);
        }
    }
}
