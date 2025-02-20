package com.dimensiondelvers.dimensiondelvers.modifier;

import com.dimensiondelvers.dimensiondelvers.modifier.effect.AbstractModifierEffect;
import com.dimensiondelvers.dimensiondelvers.modifier.source.ModifierSource;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.world.entity.Entity;

import java.util.List;

import static com.dimensiondelvers.dimensiondelvers.init.ModDatapackRegistries.MODIFIER_KEY;

public class Modifier {
    public static Codec<Modifier> DIRECT_CODEC = RecordCodecBuilder.create(inst -> inst.group(
            AbstractModifierEffect.DIRECT_CODEC.listOf().fieldOf("modifiers").forGetter(Modifier::getModifierEffects)
    ).apply(inst, Modifier::new));
    public static final Codec<Holder<Modifier>> CODEC = RegistryFixedCodec.create(MODIFIER_KEY);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<Modifier>> STREAM_CODEC = ByteBufCodecs.holderRegistry(MODIFIER_KEY);

    private final List<AbstractModifierEffect> modifierEffects;

    public Modifier(List<AbstractModifierEffect> modifierEffects) {
        this.modifierEffects = modifierEffects;
    }

    public List<AbstractModifierEffect> getModifierEffects() {
        return modifierEffects;
    }

    public void enableModifier(float roll, Entity entity, ModifierSource source){
        for(AbstractModifierEffect effect : modifierEffects){
            effect.enableModifier(roll, entity, source);
        }
    }

    public void disableModifier(float roll, Entity entity, ModifierSource source){
        for(AbstractModifierEffect effect : modifierEffects){
            effect.disableModifier(roll, entity, source);
        }
    }

}
