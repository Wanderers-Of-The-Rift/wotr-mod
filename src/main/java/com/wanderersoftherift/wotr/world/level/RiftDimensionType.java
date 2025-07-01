package com.wanderersoftherift.wotr.world.level;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.dimension.DimensionType;

import java.util.NavigableMap;
import java.util.TreeMap;

public class RiftDimensionType {
    public static final ResourceLocation RIFT_DIMENSION_RENDERER_KEY = WanderersOfTheRift.id("rift_dimension_renderer");

    public static final NavigableMap<Integer, ResourceKey<DimensionType>> RIFT_DIMENSION_TYPE_MAP = new TreeMap<>();

    static {
        for (int height = 64; height <= 384; height += 64) {
            RIFT_DIMENSION_TYPE_MAP.put(height,
                    ResourceKey.create(Registries.DIMENSION_TYPE, WanderersOfTheRift.id("rift_dimension_" + height)));
        }
        RIFT_DIMENSION_TYPE_MAP.put(1024,
                ResourceKey.create(Registries.DIMENSION_TYPE, WanderersOfTheRift.id("rift_dimension_1024")));
        RIFT_DIMENSION_TYPE_MAP.ceilingEntry(256).getValue();
    }
}
