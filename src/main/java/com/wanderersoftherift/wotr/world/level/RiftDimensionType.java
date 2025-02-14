package com.wanderersoftherift.wotr.world.level;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.dimension.DimensionType;

import java.util.OptionalLong;

public class RiftDimensionType {
    public static final DimensionType RIFT_DIMENSION;
    public static final RiftDimensionSpecialEffects RIFT_DIMENSION_RENDERER;
    public static final ResourceLocation RIFT_DIMENSION_RENDERER_KEY = WanderersOfTheRift.id("rift_dimension_renderer");

    static {
        RIFT_DIMENSION = new DimensionType(
                OptionalLong.of(0L),
                false,
                false,
                false,
                true,
                1,
                false,
                false,
                -64, // Minimum Y Level
                64 + 320, // Height + Min Y = Max Y
                64 + 320, // Logical Height
                BlockTags.INFINIBURN_OVERWORLD,
                RiftDimensionType.RIFT_DIMENSION_RENDERER_KEY,
                0f,
                new DimensionType.MonsterSettings(false, false, UniformInt.of(0, 0), 0) // unsure what values we want here
        );

        RIFT_DIMENSION_RENDERER = new RiftDimensionSpecialEffects();
    }
}
