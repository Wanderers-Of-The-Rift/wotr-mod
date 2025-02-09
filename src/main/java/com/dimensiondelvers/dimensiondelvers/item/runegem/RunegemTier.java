package com.dimensiondelvers.dimensiondelvers.item.runegem;


import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

public enum RunegemTier {

    RAW("raw"),
    SHAPED("shaped"),
    CUT("cut"),
    POLISHED("polished"),
    FRAMED("framed"),
    UNIQUE("unique");

    public static final Codec<RunegemTier> CODEC = Codec.STRING.flatComapMap(s -> RunegemTier.byName(s, null), d -> DataResult.success(d.getName()));

    private final String name;

    private RunegemTier(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static RunegemTier byName(String name, RunegemTier defaultReturn) {
        for (RunegemTier value : values()){
            if (value.name.equalsIgnoreCase(name)) {
                return value;
            }
        }
        return defaultReturn;
    }

}
