package com.wanderersoftherift.wotr.world.level.levelgen.space.corridor;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.world.level.FastRiftGenerator;
import net.minecraft.core.Direction;
import net.minecraft.server.MinecraftServer;

public record GeneratorLayout() implements SerializableCorridorValidator {

    public static final GeneratorLayout INSTANCE = new GeneratorLayout();

    public static final MapCodec<GeneratorLayout> CODEC = MapCodec.unit(INSTANCE);

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
        return generator.getOrCreateLayout(server).validateCorridor(x, y, z, d, generator, server);
    }
}
