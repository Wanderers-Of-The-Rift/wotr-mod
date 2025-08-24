package com.wanderersoftherift.wotr.init;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.modifier.source.AbilityUpgradeModifierSource;
import com.wanderersoftherift.wotr.modifier.source.AttachEffectModifierSource;
import com.wanderersoftherift.wotr.modifier.source.GearImplicitModifierSource;
import com.wanderersoftherift.wotr.modifier.source.GearSocketModifierSource;
import com.wanderersoftherift.wotr.modifier.source.ModifierSource;
import com.wanderersoftherift.wotr.serialization.DualCodec;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class WotrModifierSourceTypes {
    public static final DeferredRegister<DualCodec<? extends ModifierSource>> MODIFIER_SOURCE = DeferredRegister
            .create(WotrRegistries.MODIFIER_SOURCES, WanderersOfTheRift.MODID);

    public static final Supplier<DualCodec<? extends ModifierSource>> GEAR_IMPLICIT = MODIFIER_SOURCE
            .register("gear_implicit", () -> GearImplicitModifierSource.TYPE);
    public static final Supplier<DualCodec<? extends ModifierSource>> GEAR_SOCKET = MODIFIER_SOURCE
            .register("gear_socket", () -> GearSocketModifierSource.TYPE);
    public static final Supplier<DualCodec<? extends ModifierSource>> ATTACHED_EFFECT = MODIFIER_SOURCE
            .register("attached_effect", () -> AttachEffectModifierSource.TYPE);
    public static final Supplier<DualCodec<? extends ModifierSource>> ABILITY_UPGRADE = MODIFIER_SOURCE
            .register("ability_upgrade", () -> AbilityUpgradeModifierSource.TYPE);
}
