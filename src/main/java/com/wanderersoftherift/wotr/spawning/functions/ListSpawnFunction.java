package com.wanderersoftherift.wotr.spawning.functions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;

public record ListSpawnFunction(List<SpawnFunction> functions) implements SpawnFunction {

    public static final MapCodec<ListSpawnFunction> MAP_CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    SpawnFunction.CODEC.listOf().fieldOf("functions").forGetter(ListSpawnFunction::functions)
            ).apply(instance, ListSpawnFunction::new)
    );

    @Override
    public MapCodec<? extends SpawnFunction> codec() {
        return MAP_CODEC;
    }

    @Override
    public void applyToMob(Mob mob, BlockEntity spawner, RandomSource random) {
        functions.forEach(it -> it.applyToMob(mob, spawner, random));
    }
}
