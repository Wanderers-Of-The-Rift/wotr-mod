package com.wanderersoftherift.wotr.world.level.levelgen.space.corridor;

import com.wanderersoftherift.wotr.world.level.FastRiftGenerator;
import net.minecraft.core.Direction;
import net.minecraft.server.MinecraftServer;

/**
 * Used to determine whether a corridor exists. Can be used both before the corridor is generated and after the corridor
 * is generated.
 */
public interface CorridorValidator {

    boolean validateCorridor(int x, int y, int z, Direction d, FastRiftGenerator generator, MinecraftServer server);

}
