package com.wanderersoftherift.wotr.world.level.levelgen.space.corridor;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.world.level.FastRiftGenerator;
import net.minecraft.core.Direction;
import net.minecraft.server.MinecraftServer;

public record Identity(CorridorValidator base) implements CorridorValidator {

    public static final MapCodec<Identity> CODEC = CorridorValidator.CODEC.fieldOf("base")
            .xmap(Identity::new, Identity::base);

    @Override
    public MapCodec<? extends CorridorValidator> codec() {
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
