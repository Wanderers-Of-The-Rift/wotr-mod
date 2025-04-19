package com.wanderersoftherift.wotr.world.level.levelgen.theme;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

public enum ThemePieceType {

    POI("poi"),
    ROOM("room");

    public static final Codec<ThemePieceType> CODEC = Codec.STRING.flatComapMap(s -> ThemePieceType.byName(s, null),
            d -> DataResult.success(d.getName()));

    private final String name;

    ThemePieceType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static ThemePieceType byName(String name, ThemePieceType defaultReturn) {
        for (ThemePieceType value : values()) {
            if (value.name.equalsIgnoreCase(name)) {
                return value;
            }
        }
        return defaultReturn;
    }

}
