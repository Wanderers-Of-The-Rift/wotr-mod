package com.wanderersoftherift.wotr.util;

import net.minecraft.core.IdMapper;

import java.util.List;

/**
 * Id Mapper that exposes the item list for direct use in chunk generation
 * 
 * @param <T>
 */
public class FastIdMapper<T> extends IdMapper<T> {

    public List<T> getItems() {
        return idToT;
    }
}
