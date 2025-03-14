package com.wanderersoftherift.wotr.init;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.item.runegem.RunegemData;
import com.wanderersoftherift.wotr.modifier.Modifier;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public class ModDatapackRegistries {

    public static final ResourceKey<Registry<Modifier>> MODIFIER_KEY = ResourceKey.createRegistryKey(WanderersOfTheRift.id("modifier"));
    public static final ResourceKey<Registry<RunegemData>> RUNEGEM_DATA_KEY = ResourceKey.createRegistryKey(WanderersOfTheRift.id("runegem_data"));
}
