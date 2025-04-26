package com.wanderersoftherift.wotr.init;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.targeting.AbstractTargeting;
import com.wanderersoftherift.wotr.abilities.targeting.CubeAreaTargeting;
import com.wanderersoftherift.wotr.abilities.targeting.RaycastTargeting;
import com.wanderersoftherift.wotr.abilities.targeting.SelfTargeting;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;

import java.util.function.Supplier;

public class ModTargetingTypes {

    public static final ResourceKey<Registry<MapCodec<? extends AbstractTargeting>>> EFFECT_TARGETING_REG_KEY = ResourceKey
            .createRegistryKey(WanderersOfTheRift.id("effect_targeting"));
    public static final Registry<MapCodec<? extends AbstractTargeting>> EFFECT_TARGETING_REGISTRY = new RegistryBuilder<>(
            EFFECT_TARGETING_REG_KEY).create();
    public static final DeferredRegister<MapCodec<? extends AbstractTargeting>> TARGETING_TYPES = DeferredRegister
            .create(EFFECT_TARGETING_REG_KEY, WanderersOfTheRift.MODID);

    public static final Supplier<MapCodec<SelfTargeting>> SELF_TARGETING = TARGETING_TYPES.register("self_targeting",
            () -> SelfTargeting.CODEC);
    public static final Supplier<MapCodec<RaycastTargeting>> RAYCAST_TARGETING = TARGETING_TYPES
            .register("raycast_targeting", () -> RaycastTargeting.CODEC);
    public static final Supplier<MapCodec<CubeAreaTargeting>> AREA_TARGETING = TARGETING_TYPES
            .register("area_targeting", () -> CubeAreaTargeting.CODEC);

}
