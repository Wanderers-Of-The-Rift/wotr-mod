package com.wanderersoftherift.wotr.spawning.functions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;

public record ChanceSpawnFunction(float chance, List<Holder<SpawnFunction>> functions) implements SpawnFunction {

    public static final MapCodec<ChanceSpawnFunction> MAP_CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Codec.floatRange(0, 1).fieldOf("chance").forGetter(ChanceSpawnFunction::chance),
                    SpawnFunction.HOLDER_CODEC.listOf().fieldOf("functions").forGetter(ChanceSpawnFunction::functions)
            ).apply(instance, ChanceSpawnFunction::new)
    );

    @Override
    public MapCodec<? extends SpawnFunction> codec() {
        return MAP_CODEC;
    }

    @Override
    public void applyToMob(Mob mob, BlockEntity spawner, RandomSource random) {
        if (random.nextFloat() < chance) {
            functions.forEach(it -> it.value().applyToMob(mob, spawner, random));
        }
    }
}
