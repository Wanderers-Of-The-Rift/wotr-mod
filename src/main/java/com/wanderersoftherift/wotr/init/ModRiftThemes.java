package com.dimensiondelvers.dimensiondelvers.init;

import com.dimensiondelvers.dimensiondelvers.DimensionDelvers;
import com.dimensiondelvers.dimensiondelvers.world.level.levelgen.theme.RiftTheme;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public class ModRiftThemes {

    public static final ResourceKey<Registry<RiftTheme>> RIFT_THEME_KEY = ResourceKey.createRegistryKey(DimensionDelvers.id("rift_theme"));
}
