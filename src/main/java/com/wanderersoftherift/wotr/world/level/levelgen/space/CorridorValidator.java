package com.wanderersoftherift.wotr.world.level.levelgen.space;

import net.minecraft.core.Direction;
import net.minecraft.server.MinecraftServer;

/**
 * checks if corridor is real
 */
public interface CorridorValidator {

    CorridorValidator INVALID = (x, y, z, d, server) -> false;

    boolean validateCorridor(int x, int y, int z, Direction d, MinecraftServer server);

}
