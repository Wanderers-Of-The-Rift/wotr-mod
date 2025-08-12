package com.wanderersoftherift.wotr.init.ability;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.targeting.AbilityTargeting;
import com.wanderersoftherift.wotr.abilities.targeting.CubeAreaTargeting;
import com.wanderersoftherift.wotr.abilities.targeting.RaycastTargeting;
import com.wanderersoftherift.wotr.abilities.targeting.SelfTargeting;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class WotrTargetingTypes {

    public static final DeferredRegister<MapCodec<? extends AbilityTargeting>> TARGETING_TYPES = DeferredRegister
            .create(WotrRegistries.Keys.EFFECT_TARGETING_TYPES, WanderersOfTheRift.MODID);

    public static final Supplier<MapCodec<SelfTargeting>> SELF_TARGETING = TARGETING_TYPES.register("self_targeting",
            () -> SelfTargeting.CODEC);
    public static final Supplier<MapCodec<RaycastTargeting>> RAYCAST_TARGETING = TARGETING_TYPES
            .register("raycast_targeting", () -> RaycastTargeting.CODEC);
    public static final Supplier<MapCodec<CubeAreaTargeting>> AREA_TARGETING = TARGETING_TYPES
            .register("area_targeting", () -> CubeAreaTargeting.CODEC);

}
