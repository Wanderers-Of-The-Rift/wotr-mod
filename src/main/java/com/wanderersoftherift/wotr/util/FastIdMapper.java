package com.wanderersoftherift.wotr.util;

import net.minecraft.core.IdMapper;

import java.util.List;

public class FastIdMapper<T> extends IdMapper<T> {

    public List<T> getItems() {
        return idToT;
    }
}
