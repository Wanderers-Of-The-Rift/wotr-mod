package com.wanderersoftherift.wotr.world.level.levelgen.space.corridor;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.world.level.FastRiftGenerator;
import net.minecraft.core.Direction;
import net.minecraft.server.MinecraftServer;

import java.util.List;

public record And(List<SerializableCorridorValidator> base) implements SerializableCorridorValidator {

    public static final MapCodec<And> CODEC = SerializableCorridorValidator.CODEC.listOf()
            .fieldOf("values")
            .xmap(And::new, And::base);

    public And(SerializableCorridorValidator... values) {
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
        return base.stream().allMatch(it -> it.validateCorridor(x, y, z, d, generator, server));
    }
}
