package com.wanderersoftherift.wotr.spawning.functions;

import com.mojang.serialization.MapCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

public record MoveSpawnFunction(Vec3 movement) implements SpawnFunction {

    public static final MapCodec<MoveSpawnFunction> MAP_CODEC = Vec3.CODEC.fieldOf("movement")
            .xmap(MoveSpawnFunction::new, MoveSpawnFunction::movement);

    @Override
    public MapCodec<? extends SpawnFunction> codec() {
        return MAP_CODEC;
    }

    @Override
    public void applyToMob(Entity entity, BlockEntity spawner, RandomSource random) {
        entity.teleportRelative(movement.x, movement.y, movement.z);
    }
}
