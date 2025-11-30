package com.wanderersoftherift.wotr.init.ability;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.targeting.AbilityTargeting;
import com.wanderersoftherift.wotr.abilities.targeting.AreaTargeting;
import com.wanderersoftherift.wotr.abilities.targeting.CasterTargeting;
import com.wanderersoftherift.wotr.abilities.targeting.ConnectedBlockTargeting;
import com.wanderersoftherift.wotr.abilities.targeting.FieldOfViewTargeting;
import com.wanderersoftherift.wotr.abilities.targeting.FilterTargeting;
import com.wanderersoftherift.wotr.abilities.targeting.OffsetTargeting;
import com.wanderersoftherift.wotr.abilities.targeting.RandomChanceTargeting;
import com.wanderersoftherift.wotr.abilities.targeting.RandomSubsetTargeting;
import com.wanderersoftherift.wotr.abilities.targeting.RaycastTargeting;
import com.wanderersoftherift.wotr.abilities.targeting.SourceTargeting;
import com.wanderersoftherift.wotr.abilities.targeting.TriggerTargetTargeting;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class WotrTargetingTypes {

    public static final DeferredRegister<MapCodec<? extends AbilityTargeting>> TARGETING_TYPES = DeferredRegister
            .create(WotrRegistries.Keys.EFFECT_TARGETING_TYPES, WanderersOfTheRift.MODID);

    public static final Supplier<MapCodec<FilterTargeting>> SELF = TARGETING_TYPES.register("self",
            () -> FilterTargeting.CODEC);
    public static final Supplier<MapCodec<RaycastTargeting>> RAYCAST = TARGETING_TYPES.register("raycast",
            () -> RaycastTargeting.CODEC);
    public static final Supplier<MapCodec<FieldOfViewTargeting>> FIELD_OF_VIEW = TARGETING_TYPES
            .register("field_of_view", () -> FieldOfViewTargeting.CODEC);
    public static final Supplier<MapCodec<AreaTargeting>> AREA = TARGETING_TYPES.register("area",
            () -> AreaTargeting.CODEC);
    public static final Supplier<MapCodec<CasterTargeting>> CASTER = TARGETING_TYPES.register("caster",
            () -> CasterTargeting.CODEC);
    public static final Supplier<MapCodec<SourceTargeting>> SOURCE = TARGETING_TYPES.register("source",
            () -> SourceTargeting.CODEC);
    public static final Supplier<MapCodec<RandomSubsetTargeting>> RANDOM_SUBSET = TARGETING_TYPES
            .register("random_subset", () -> RandomSubsetTargeting.CODEC);
    public static final Supplier<MapCodec<TriggerTargetTargeting>> TRIGGER_TARGET = TARGETING_TYPES
            .register("trigger_target", () -> TriggerTargetTargeting.CODEC);
    public static final Supplier<MapCodec<ConnectedBlockTargeting>> CONNECTED_BLOCK = TARGETING_TYPES
            .register("connected_block", () -> ConnectedBlockTargeting.CODEC);
    public static final Supplier<MapCodec<RandomChanceTargeting>> RANDOM_CHANCE = TARGETING_TYPES
            .register("random_chance", () -> RandomChanceTargeting.CODEC);
    public static final Supplier<MapCodec<OffsetTargeting>> OFFSET = TARGETING_TYPES.register("offset",
            () -> OffsetTargeting.CODEC);

}
