package com.wanderersoftherift.wotr.init;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.modifier.Modifier;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public class ModModifiers {

    public static final ResourceKey<Registry<Modifier>> MODIFIER_KEY = ResourceKey.createRegistryKey(WanderersOfTheRift.id("modifier"));
}
