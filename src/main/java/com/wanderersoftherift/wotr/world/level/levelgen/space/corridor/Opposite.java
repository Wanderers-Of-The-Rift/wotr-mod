package com.wanderersoftherift.wotr.world.level.levelgen.space.corridor;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.world.level.FastRiftGenerator;
import net.minecraft.core.Direction;
import net.minecraft.server.MinecraftServer;

public record Opposite(CorridorValidator base) implements CorridorValidator {

    public static final MapCodec<Opposite> CODEC = CorridorValidator.CODEC.fieldOf("base")
            .xmap(Opposite::new, Opposite::base);

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
        return base().validateCorridor(x + d.getStepX(), y + d.getStepY(), z + d.getStepZ(), d.getOpposite(), generator,
                server);
    }
}
