package com.wanderersoftherift.wotr.init;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.item.ability.AbilityModifier;
import com.wanderersoftherift.wotr.modifier.effect.AbstractModifierEffect;
import com.wanderersoftherift.wotr.modifier.effect.AttributeModifierEffect;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class WotrModifierEffectTypes {

    // Creating the Deferred Register
    public static final DeferredRegister<MapCodec<? extends AbstractModifierEffect>> MODIFIER_EFFECT_TYPES = DeferredRegister
            .create(WotrRegistries.Keys.MODIFIER_EFFECT_TYPES, WanderersOfTheRift.MODID);

    public static final Supplier<MapCodec<? extends AbstractModifierEffect>> ATTRIBUTE_MODIFIER = MODIFIER_EFFECT_TYPES
            .register("attribute", () -> AttributeModifierEffect.MODIFIER_CODEC);
    public static final Supplier<MapCodec<? extends AbstractModifierEffect>> ABILITY_MODIFIER = MODIFIER_EFFECT_TYPES
            .register("ability", () -> AbilityModifier.CODEC);
}
