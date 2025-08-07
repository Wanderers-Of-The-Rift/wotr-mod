package com.wanderersoftherift.wotr.world.level.levelgen.space;

import com.wanderersoftherift.wotr.world.level.FastRiftGenerator;
import net.minecraft.core.Direction;

/**
 * checks if corridor is real
 */
public interface CorridorValidator {

    boolean validateCorridor(int x, int y, int z, Direction d, FastRiftGenerator generator);

}
