package com.wanderersoftherift.wotr.world.level.levelgen.processor.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

public enum StructureRandomType {
    BLOCK,
    PIECE,
    STRUCTURE,
    WORLD;

    public static final Codec<StructureRandomType> RANDOM_TYPE_CODEC = Codec.STRING
            .flatComapMap(s -> StructureRandomType.byName(s, null), d -> DataResult.success(d.name()));

    public static StructureRandomType byName(String input, StructureRandomType defaultStructureRandomType) {
        for (StructureRandomType enumEntry : values()) {
            if (enumEntry.name().equals(input.toUpperCase())) {
                return enumEntry;
            }
        }

        return defaultStructureRandomType;
    }
}