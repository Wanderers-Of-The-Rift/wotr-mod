package com.wanderersoftherift.wotr.world.level.levelgen.space.corridor;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.world.level.FastRiftGenerator;
import net.minecraft.core.Direction;
import net.minecraft.server.MinecraftServer;

public record Inverted(SerializableCorridorValidator base) implements SerializableCorridorValidator {

    public static final MapCodec<Inverted> CODEC = SerializableCorridorValidator.CODEC.fieldOf("base")
            .xmap(Inverted::new, Inverted::base);

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
        return !base().validateCorridor(x, y, z, d, generator, server);
    }
}
