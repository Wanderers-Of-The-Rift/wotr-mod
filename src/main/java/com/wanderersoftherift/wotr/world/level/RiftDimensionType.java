package com.wanderersoftherift.wotr.world.level;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.dimension.DimensionType;

public class RiftDimensionType {
    public static final RiftDimensionSpecialEffects RIFT_DIMENSION_RENDERER;
    public static final ResourceLocation RIFT_DIMENSION_RENDERER_KEY = WanderersOfTheRift.id("rift_dimension_renderer");
    public static final ResourceKey<DimensionType> RIFT_DIMENSION_TYPE = ResourceKey.create(Registries.DIMENSION_TYPE, WanderersOfTheRift.id("rift_dimension"));

    static {
        RIFT_DIMENSION_RENDERER = new RiftDimensionSpecialEffects();
    }
}
