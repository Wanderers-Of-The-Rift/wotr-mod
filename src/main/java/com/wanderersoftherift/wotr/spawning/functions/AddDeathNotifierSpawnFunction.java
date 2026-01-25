package com.wanderersoftherift.wotr.spawning.functions;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.block.blockentity.anomaly.DeathNotifierAttachment;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.entity.BlockEntity;

public record AddDeathNotifierSpawnFunction() implements SpawnFunction {
    public static final AddDeathNotifierSpawnFunction INSTANCE = new AddDeathNotifierSpawnFunction();
    public static final MapCodec<AddDeathNotifierSpawnFunction> MAP_CODEC = MapCodec.unit(INSTANCE);

    @Override
    public MapCodec<? extends SpawnFunction> codec() {
        return MAP_CODEC;
    }

    @Override
    public void applyToMob(Mob mob, BlockEntity spawner, RandomSource random) {
        mob.setData(WotrAttachments.DEATH_NOTIFICATION, new DeathNotifierAttachment(spawner.getBlockPos()));
    }
}
