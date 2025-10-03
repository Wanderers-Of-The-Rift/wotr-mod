package com.wanderersoftherift.wotr.init;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.item.ability.AbilityModifier;
import com.wanderersoftherift.wotr.modifier.effect.AttributeModifierEffect;
import com.wanderersoftherift.wotr.modifier.effect.EnchantmentModifierEffect;
import com.wanderersoftherift.wotr.modifier.effect.EnhanceAbilityModifierEffect;
import com.wanderersoftherift.wotr.modifier.effect.ModifierEffect;
import com.wanderersoftherift.wotr.modifier.effect.ProvideAbilityConditionModifierEffect;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class WotrModifierEffectTypes {

    // Creating the Deferred Register
    public static final DeferredRegister<MapCodec<? extends ModifierEffect>> MODIFIER_EFFECT_TYPES = DeferredRegister
            .create(WotrRegistries.Keys.MODIFIER_EFFECT_TYPES, WanderersOfTheRift.MODID);

    public static final Supplier<MapCodec<? extends ModifierEffect>> ATTRIBUTE_MODIFIER = MODIFIER_EFFECT_TYPES
            .register("attribute", () -> AttributeModifierEffect.MODIFIER_CODEC);
    public static final Supplier<MapCodec<? extends ModifierEffect>> ABILITY_MODIFIER = MODIFIER_EFFECT_TYPES
            .register("ability", () -> AbilityModifier.CODEC);

    public static final Supplier<MapCodec<? extends ModifierEffect>> ABILITY_ENHANCING_MODIFIER = MODIFIER_EFFECT_TYPES
            .register("ability_enhancement", () -> EnhanceAbilityModifierEffect.MODIFIER_CODEC);
    public static final Supplier<MapCodec<? extends ModifierEffect>> ABILITY_CONDITIONING_MODIFIER = MODIFIER_EFFECT_TYPES
            .register("ability_condition", () -> ProvideAbilityConditionModifierEffect.MODIFIER_CODEC);
    public static final Supplier<MapCodec<? extends ModifierEffect>> ENCHANT_MODIFIER = MODIFIER_EFFECT_TYPES
            .register("enchant", () -> EnchantmentModifierEffect.CODEC);
}
