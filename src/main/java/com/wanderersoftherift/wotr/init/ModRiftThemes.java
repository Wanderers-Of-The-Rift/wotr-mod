package com.wanderersoftherift.wotr.init;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.world.level.levelgen.theme.RiftTheme;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public class ModRiftThemes {

    public static final ResourceKey<Registry<RiftTheme>> RIFT_THEME_KEY = ResourceKey.createRegistryKey(WanderersOfTheRift.id("rift_theme"));
}
