package com.wanderersoftherift.wotr.world.level.levelgen.space.corridor;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.world.level.FastRiftGenerator;
import net.minecraft.core.Direction;
import net.minecraft.server.MinecraftServer;

import java.util.List;

public record Or(List<CorridorValidator> base) implements CorridorValidator {

    public static final MapCodec<Or> CODEC = CorridorValidator.CODEC.listOf().fieldOf("values").xmap(Or::new, Or::base);

    public Or(CorridorValidator... values) {
        this(ImmutableList.copyOf(values));
    }

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
        return base.stream().anyMatch(it -> it.validateCorridor(x, y, z, d, generator, server));
    }
}
