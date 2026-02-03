package com.wanderersoftherift.wotr.spawning.functions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.serialization.LaxRegistryCodec;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.function.Function;

public interface SpawnFunction {
    Codec<SpawnFunction> CODEC = WotrRegistries.SPAWN_FUNCTION_TYPES.byNameCodec()
            .dispatch(SpawnFunction::codec, Function.identity());
    Codec<Holder<SpawnFunction>> HOLDER_CODEC = LaxRegistryCodec.refOrDirect(WotrRegistries.Keys.SPAWN_FUNCTIONS,
            CODEC);

    MapCodec<? extends SpawnFunction> codec();

    void applyToMob(Entity entity, BlockEntity spawner, RandomSource random);
}
