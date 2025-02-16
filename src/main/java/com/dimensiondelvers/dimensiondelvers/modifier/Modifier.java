package com.dimensiondelvers.dimensiondelvers.modifier;

import com.dimensiondelvers.dimensiondelvers.modifier.effect.AbstractModifierEffect;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFixedCodec;

import java.util.List;

import static com.dimensiondelvers.dimensiondelvers.init.ModModifiers.MODIFIER_KEY;

public class Modifier {
    public static Codec<Modifier> DIRECT_CODEC = RecordCodecBuilder.create(inst -> inst.group(
            AbstractModifierEffect.DIRECT_CODEC.listOf().fieldOf("modifiers").forGetter(Modifier::getModifierEffects)
    ).apply(inst, Modifier::new));
    public static final Codec<Holder<Modifier>> CODEC = RegistryFixedCodec.create(MODIFIER_KEY);

    private final List<AbstractModifierEffect> modifierEffects;

    public Modifier(List<AbstractModifierEffect> modifierEffects) {
        this.modifierEffects = modifierEffects;
    }

    public List<AbstractModifierEffect> getModifierEffects() {
        return modifierEffects;
    }
}
