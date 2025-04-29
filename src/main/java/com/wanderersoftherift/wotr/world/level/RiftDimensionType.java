package com.wanderersoftherift.wotr.world.level;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.dimension.DimensionType;

import java.util.List;
import java.util.stream.IntStream;

public class RiftDimensionType {
    public static final ResourceLocation RIFT_DIMENSION_RENDERER_KEY = WanderersOfTheRift.id("rift_dimension_renderer");
    public static final List<ResourceKey<DimensionType>> RIFT_DIMENSION_TYPES = IntStream.rangeClosed(1,6).mapToObj(//creating many different dimension types might be actually stupid
            (it)->ResourceKey.create(Registries.DIMENSION_TYPE, WanderersOfTheRift.id("rift_dimension_"+(64*it)))
            ).toList();
}
