package com.wanderersoftherift.wotr.init;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;

public class WotrDamageTypes {
    public static final ResourceKey<DamageType> FIRE_DAMAGE = ResourceKey.create(Registries.DAMAGE_TYPE,
            WanderersOfTheRift.id("fire"));
    public static final ResourceKey<DamageType> ICE_DAMAGE = ResourceKey.create(Registries.DAMAGE_TYPE,
            WanderersOfTheRift.id("ice"));
    public static final ResourceKey<DamageType> POISON_DAMAGE = ResourceKey.create(Registries.DAMAGE_TYPE,
            WanderersOfTheRift.id("poison"));
    public static final ResourceKey<DamageType> LIGHTNING_DAMAGE = ResourceKey.create(Registries.DAMAGE_TYPE,
            WanderersOfTheRift.id("lightning"));
    public static final ResourceKey<DamageType> EARTH_DAMAGE = ResourceKey.create(Registries.DAMAGE_TYPE,
            WanderersOfTheRift.id("earth"));
    public static final ResourceKey<DamageType> FIRE_BURN_DAMAGE = ResourceKey.create(Registries.DAMAGE_TYPE,
            WanderersOfTheRift.id("fire_burn"));
    public static final ResourceKey<DamageType> THORNS_DAMAGE = ResourceKey.create(Registries.DAMAGE_TYPE,
            WanderersOfTheRift.id("thorns"));
}
