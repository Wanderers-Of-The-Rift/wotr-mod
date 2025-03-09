package com.wanderersoftherift.wotr.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

public enum RiftChestType {

    WOODEN("wooden");

    public static final Codec<RiftChestType> CODEC = Codec.STRING.flatComapMap(s -> RiftChestType.byName(s, null), d -> DataResult.success(d.getName()));

    private final String name;

    RiftChestType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static RiftChestType byName(String name, RiftChestType defaultReturn) {
        for (RiftChestType value : values()) {
            if (value.name.equalsIgnoreCase(name)) {
                return value;
            }
        }
        return defaultReturn;
    }

}

