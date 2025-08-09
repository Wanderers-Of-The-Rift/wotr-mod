package com.wanderersoftherift.wotr.init.ability;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.TrackedAbilityTrigger;
import com.wanderersoftherift.wotr.abilities.triggers.TakeDamageTrigger;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class WotrTrackedAbilityTriggers {
    public static final DeferredRegister<MapCodec<? extends TrackedAbilityTrigger>> TRIGGERS = DeferredRegister
            .create(WotrRegistries.Keys.TRACKED_ABILITY_TRIGGERS, WanderersOfTheRift.MODID);

    public static final Supplier<MapCodec<? extends TrackedAbilityTrigger>> TAKE_DAMAGE = TRIGGERS
            .register("take_damage", () -> TakeDamageTrigger.CODEC);
}
