package com.dimensiondelvers.dimensiondelvers.item.runegem;


import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

public enum RunegemShape {

    CIRCLE("circle"),
    SQUARE("square"),
    TRIANGLE("triangle"),
    DIAMOND("diamond"),
    HEART("heart"),
    PENTAGON("pentagon");

    public static final Codec<RunegemShape> CODEC = Codec.STRING.flatComapMap(s -> RunegemShape.byName(s, null), d -> DataResult.success(d.getName()));

    private final String name;

    RunegemShape(String name) {
        this.name = name;
    }

    public static RunegemShape byName(String name, RunegemShape defaultReturn) {
        for (RunegemShape value : values()) {
            if (value.name.equalsIgnoreCase(name)) {
                return value;
            }
        }
        return defaultReturn;
    }

    public String getName() {
        return name;
    }

}
