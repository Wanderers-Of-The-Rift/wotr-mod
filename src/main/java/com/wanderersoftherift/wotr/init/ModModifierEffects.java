package com.wanderersoftherift.wotr.init;


import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.modifier.effect.AbstractModifierEffect;
import com.wanderersoftherift.wotr.modifier.effect.AttributeModifierEffect;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;

import java.util.function.Supplier;


public class ModModifierEffects {

    // Registry and key for Abstract Modifiers
    public static final ResourceKey<Registry<MapCodec<? extends AbstractModifierEffect>>> MODIFIER_EFFECT_TYPE_KEY = ResourceKey.createRegistryKey(WanderersOfTheRift.id("modifier_effect_type"));

    public static final ResourceKey<Registry<AbstractModifierEffect>> MODIFIER_EFFECT_KEY = ResourceKey.createRegistryKey(WanderersOfTheRift.id("modifier_effect"));

    public static final Registry<MapCodec<? extends AbstractModifierEffect>> MODIFIER_TYPE_REGISTRY = new RegistryBuilder<>(MODIFIER_EFFECT_TYPE_KEY).create();

    // Creating the Deferred Register
    public static final DeferredRegister<MapCodec<? extends AbstractModifierEffect>> MODIFIER_EFFECT_TYPES = DeferredRegister.create(
            MODIFIER_EFFECT_TYPE_KEY, WanderersOfTheRift.MODID
    );

    public static final Supplier<MapCodec<? extends AbstractModifierEffect>> ATTRIBUTE_MODIFIER = MODIFIER_EFFECT_TYPES.register(
            "attribute", () -> AttributeModifierEffect.MODIFIER_CODEC
    );
}
