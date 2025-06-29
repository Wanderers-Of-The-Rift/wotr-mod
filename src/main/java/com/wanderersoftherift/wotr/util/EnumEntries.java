package com.wanderersoftherift.wotr.util;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.Direction;

import static net.minecraft.core.Direction.Plane;

public class EnumEntries {
    public static final ImmutableList<Direction> DIRECTIONS = ImmutableList.copyOf(Direction.values());
    public static final ImmutableList<Direction> DIRECTIONS_HORIZONTAL = ImmutableList.copyOf(Plane.HORIZONTAL);
}
