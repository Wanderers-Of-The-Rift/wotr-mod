package com.wanderersoftherift.wotr.init.ability;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.TrackedAbilityTrigger;
import com.wanderersoftherift.wotr.abilities.triggers.DealDamageTrigger;
import com.wanderersoftherift.wotr.abilities.triggers.TakeDamageTrigger;
import com.wanderersoftherift.wotr.abilities.triggers.TickTrigger;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class WotrTrackedAbilityTriggers {
    public static final DeferredRegister<TrackedAbilityTrigger.Type<?>> TRIGGERS = DeferredRegister
            .create(WotrRegistries.Keys.TRACKED_ABILITY_TRIGGERS, WanderersOfTheRift.MODID);

    public static final DeferredHolder<TrackedAbilityTrigger.Type<?>, TrackedAbilityTrigger.Type<TickTrigger>> TICK_TRIGGER = TRIGGERS
            .register("tick", () -> TickTrigger.TYPE);
    public static final DeferredHolder<TrackedAbilityTrigger.Type<?>, TrackedAbilityTrigger.Type<TakeDamageTrigger>> TAKE_DAMAGE = TRIGGERS
            .register("take_damage", () -> TakeDamageTrigger.TYPE);
    public static final DeferredHolder<TrackedAbilityTrigger.Type<?>, TrackedAbilityTrigger.Type<DealDamageTrigger>> DEAL_DAMAGE = TRIGGERS
            .register("deal_damage", () -> DealDamageTrigger.TYPE);
}
