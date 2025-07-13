package com.wanderersoftherift.wotr.entity.portal;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

/**
 * Defines the location a portal spawns
 * 
 * @param position  The exact position the portal is spawning (specifically, the base of the portal)
 * @param direction The direction the portal is facing
 */
public record PortalSpawnLocation(Vec3 position, Direction direction) {
    public static final BlockPos DEFAULT_RIFT_EXIT_POSITION = new BlockPos(8, 6, 8);
}
