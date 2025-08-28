package com.wanderersoftherift.wotr.world.level.levelgen.space.corridor;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.world.level.FastRiftGenerator;
import com.wanderersoftherift.wotr.world.level.levelgen.space.VoidRiftSpace;
import net.minecraft.core.Direction;
import net.minecraft.server.MinecraftServer;

public record GeneratorLayout() implements CorridorValidator {

    public static final GeneratorLayout INSTANCE = new GeneratorLayout();

    public static final MapCodec<GeneratorLayout> CODEC = MapCodec.unit(INSTANCE);

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

        var space = generator.getOrCreateLayout(server).getChunkSpace(x, y, z);
        if (space == null || space instanceof VoidRiftSpace) {
            return false;
        }
        var spaceOrigin = space.origin();
        var dx = x - spaceOrigin.getX();
        var dy = y - spaceOrigin.getY();
        var dz = z - spaceOrigin.getZ();
        for (var corridor : space.corridors()) {
            if (corridor.direction() == d && corridor.position().getX() == dx && corridor.position().getY() == dy
                    && corridor.position().getZ() == dz) {
                return true;
            }
        }
        return false;
    }
}
