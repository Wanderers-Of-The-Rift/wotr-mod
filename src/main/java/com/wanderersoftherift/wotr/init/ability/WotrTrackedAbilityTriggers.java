package com.wanderersoftherift.wotr.init.ability;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.TrackableTrigger;
import com.wanderersoftherift.wotr.abilities.triggers.DealDamageTrigger;
import com.wanderersoftherift.wotr.abilities.triggers.KillTrigger;
import com.wanderersoftherift.wotr.abilities.triggers.LootTrigger;
import com.wanderersoftherift.wotr.abilities.triggers.TakeDamageTrigger;
import com.wanderersoftherift.wotr.abilities.triggers.TickTrigger;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class WotrTrackedAbilityTriggers {
    public static final DeferredRegister<TrackableTrigger.TriggerType<?>> TRIGGERS = DeferredRegister
            .create(WotrRegistries.Keys.TRACKED_ABILITY_TRIGGERS, WanderersOfTheRift.MODID);

    public static final DeferredHolder<TrackableTrigger.TriggerType<?>, TrackableTrigger.TriggerType<TickTrigger>> TICK_TRIGGER = TRIGGERS
            .register("tick", () -> TickTrigger.TRIGGER_TYPE);
    public static final DeferredHolder<TrackableTrigger.TriggerType<?>, TrackableTrigger.TriggerType<TakeDamageTrigger>> TAKE_DAMAGE = TRIGGERS
            .register("take_damage", () -> TakeDamageTrigger.TRIGGER_TYPE);
    public static final DeferredHolder<TrackableTrigger.TriggerType<?>, TrackableTrigger.TriggerType<DealDamageTrigger>> DEAL_DAMAGE = TRIGGERS
            .register("deal_damage", () -> DealDamageTrigger.TRIGGER_TYPE);
    public static final DeferredHolder<TrackableTrigger.TriggerType<?>, TrackableTrigger.TriggerType<LootTrigger>> LOOT = TRIGGERS
            .register("loot", () -> LootTrigger.TRIGGER_TYPE);
    public static final DeferredHolder<TrackableTrigger.TriggerType<?>, TrackableTrigger.TriggerType<KillTrigger>> KILL = TRIGGERS
            .register("kill", () -> KillTrigger.TRIGGER_TYPE);
}
