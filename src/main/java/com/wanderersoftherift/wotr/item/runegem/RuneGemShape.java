package com.wanderersoftherift.wotr.item.runegem;


import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

public enum RuneGemShape {

    CIRCLE("circle"),
    SQUARE("square"),
    TRIANGLE("triangle"),
    DIAMOND("diamond"),
    HEART("heart"),
    PENTAGON("pentagon");

    public static final Codec<RuneGemShape> CODEC = Codec.STRING.flatComapMap(s -> RuneGemShape.byName(s, null), d -> DataResult.success(d.getName()));

    private final String name;

    RuneGemShape(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static RuneGemShape byName(String name, RuneGemShape defaultReturn) {
        for (RuneGemShape value : values()){
            if (value.name.equalsIgnoreCase(name)) {
                return value;
            }
        }
        return defaultReturn;
    }

}
