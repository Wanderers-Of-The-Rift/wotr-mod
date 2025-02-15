package com.dimensiondelvers.dimensiondelvers.init;


import com.dimensiondelvers.dimensiondelvers.DimensionDelvers;
import com.dimensiondelvers.dimensiondelvers.modifier.effect.AbstractModifierEffect;
import com.dimensiondelvers.dimensiondelvers.modifier.effect.AttributeModifierEffect;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.Registry;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;
import java.util.function.Supplier;


public class ModModifierEffects {

    // Registry and key for Abstract Modifiers
    public static final ResourceKey<Registry<MapCodec<? extends AbstractModifierEffect>>> MODIFIER_EFFECT_TYPE_KEY = ResourceKey.createRegistryKey(DimensionDelvers.id("modifier_effect_type"));

    public static final ResourceKey<Registry<AbstractModifierEffect>> MODIFIER_EFFECT_KEY = ResourceKey.createRegistryKey(DimensionDelvers.id("modifier_effect"));

    public static final Registry<MapCodec<? extends AbstractModifierEffect>> MODIFIER_TYPE_REGISTRY = new RegistryBuilder<>(MODIFIER_EFFECT_TYPE_KEY).create();

    // Creating the Deferred Register
    public static final DeferredRegister<MapCodec<? extends AbstractModifierEffect>> MODIFIER_EFFECT_TYPES = DeferredRegister.create(
            MODIFIER_EFFECT_TYPE_KEY, DimensionDelvers.MODID
    );

    public static final Supplier<MapCodec<? extends AbstractModifierEffect>> ATTRIBUTE_MODIFIER = MODIFIER_EFFECT_TYPES.register(
            "attribute_modifier", () -> AttributeModifierEffect.MODIFIER_CODEC
    );
}
