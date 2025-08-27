package com.wanderersoftherift.wotr.world.level.levelgen.space.corridor;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.world.level.FastRiftGenerator;
import net.minecraft.core.Direction;
import net.minecraft.server.MinecraftServer;

public record Identity(SerializableCorridorValidator base) implements SerializableCorridorValidator {

    public static final MapCodec<Identity> CODEC = SerializableCorridorValidator.CODEC.fieldOf("base")
            .xmap(Identity::new, Identity::base);

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
        return base().validateCorridor(x, y, z, d, generator, server);
    }
}
