package com.wanderersoftherift.wotr.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.modifier.effect.AbstractModifierEffect;
import com.wanderersoftherift.wotr.modifier.source.ModifierSource;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static com.wanderersoftherift.wotr.init.ModModifiers.MODIFIER_KEY;

public class Modifier {
    public static final Codec<Modifier> DIRECT_CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.INT.fieldOf("tier").forGetter(Modifier::getTier),
            AbstractModifierEffect.DIRECT_CODEC.listOf().fieldOf("modifiers").forGetter(Modifier::getModifierEffects))
            .apply(inst, Modifier::new));
    public static final Codec<Holder<Modifier>> CODEC = RegistryFixedCodec.create(MODIFIER_KEY);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<Modifier>> STREAM_CODEC = ByteBufCodecs
            .holderRegistry(MODIFIER_KEY);

    private final int tier;
    private final List<AbstractModifierEffect> modifierEffects;

    public Modifier(int tier, List<AbstractModifierEffect> modifierEffects) {
        this.tier = tier;
        this.modifierEffects = modifierEffects;
    }

    public int getTier() {
        return tier;
    }

    public List<AbstractModifierEffect> getModifierEffects() {
        return modifierEffects;
    }

    public void enableModifier(float roll, Entity entity, ModifierSource source) {
        for (AbstractModifierEffect effect : modifierEffects) {
            effect.enableModifier(roll, entity, source);
        }
    }

    public void disableModifier(float roll, Entity entity, ModifierSource source) {
        for (AbstractModifierEffect effect : modifierEffects) {
            effect.disableModifier(roll, entity, source);
        }
    }

    public List<TooltipComponent> getTooltipComponent(ItemStack stack, float roll, ModifierInstance instance,
            ChatFormatting chatFormatting) {
        List<TooltipComponent> tooltipComponents = new ArrayList<>();
        for (AbstractModifierEffect effect : modifierEffects) {
            tooltipComponents.add(effect.getTooltipComponent(stack, roll, chatFormatting));
        }
        return tooltipComponents;
    }
}