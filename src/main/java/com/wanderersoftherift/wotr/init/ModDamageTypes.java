package com.wanderersoftherift.wotr.init;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;

public class ModDamageTypes {
    public static final ResourceKey<DamageType> FIRE_DAMAGE = ResourceKey.create(Registries.DAMAGE_TYPE,
            WanderersOfTheRift.id("fire"));
    public static final ResourceKey<DamageType> FIRE_BURN_DAMAGE = ResourceKey.create(Registries.DAMAGE_TYPE,
            WanderersOfTheRift.id("fire_burn"));
}
