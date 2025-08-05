package com.wanderersoftherift.wotr.world.level.levelgen.space;

import net.minecraft.core.Direction;

/**
 * checks if corridor is real
 */
public interface CorridorValidator {

    CorridorValidator INVALID = (x, y, z, d) -> false;

    boolean validateCorridor(int x, int y, int z, Direction d);

    static CorridorValidator and(CorridorValidator a, CorridorValidator b) {
        return (x, y, z, dir) -> a.validateCorridor(x, y, z, dir) && b.validateCorridor(x, y, z, dir);
    }

    static CorridorValidator or(CorridorValidator a, CorridorValidator b) {
        return (x, y, z, dir) -> a.validateCorridor(x, y, z, dir) || b.validateCorridor(x, y, z, dir);
    }

    static CorridorValidator opposite(CorridorValidator base) {
        return (x, y, z, dir) -> base.validateCorridor(x + dir.getStepX(), y + dir.getStepY(), z + dir.getStepZ(),
                dir.getOpposite());
    }

}
