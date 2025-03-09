package com.wanderersoftherift.wotr.item.runegem;


import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

public enum RuneGemTier {

    RAW("raw"),
    SHAPED("shaped"),
    CUT("cut"),
    POLISHED("polished"),
    FRAMED("framed"),
    UNIQUE("unique");

    public static final Codec<RuneGemTier> CODEC = Codec.STRING.flatComapMap(s -> RuneGemTier.byName(s, null), d -> DataResult.success(d.getName()));

    private final String name;

    private RuneGemTier(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static RuneGemTier byName(String name, RuneGemTier defaultReturn) {
        for (RuneGemTier value : values()){
            if (value.name.equalsIgnoreCase(name)) {
                return value;
            }
        }
        return defaultReturn;
    }

}
