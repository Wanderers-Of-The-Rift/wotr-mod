package com.dimensiondelvers.dimensiondelvers.init;

import com.dimensiondelvers.dimensiondelvers.DimensionDelvers;
import com.dimensiondelvers.dimensiondelvers.modifier.Modifier;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public class ModModifiers {

    public static final ResourceKey<Registry<Modifier>> MODIFIER_KEY = ResourceKey.createRegistryKey(DimensionDelvers.id("modifier"));
}
