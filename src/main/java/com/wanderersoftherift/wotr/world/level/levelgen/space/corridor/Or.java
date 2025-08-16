package com.wanderersoftherift.wotr.world.level.levelgen.space.corridor;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.world.level.FastRiftGenerator;
import net.minecraft.core.Direction;
import net.minecraft.server.MinecraftServer;

import java.util.List;

public record Or(List<SerializableCorridorValidator> base) implements SerializableCorridorValidator {

    public static final MapCodec<com.wanderersoftherift.wotr.world.level.levelgen.space.corridor.Or> CODEC = SerializableCorridorValidator.CODEC
            .listOf()
            .fieldOf("values")
            .xmap(com.wanderersoftherift.wotr.world.level.levelgen.space.corridor.Or::new,
                    com.wanderersoftherift.wotr.world.level.levelgen.space.corridor.Or::base);

    public Or(SerializableCorridorValidator... values) {
        this(ImmutableList.copyOf(values));
    }

    @Override
    public MapCodec<? extends SerializableCorridorValidator> codec() {
        return CODEC;
    }

    @Override
    public boolean validateCorridor(
            int x,
            int y,
            int z,
            Direction d,
            FastRiftGenerator generator,
            MinecraftServer server) {
        return base.stream().anyMatch(it -> it.validateCorridor(x, y, z, d, generator, server));
    }
}
