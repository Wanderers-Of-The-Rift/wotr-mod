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

public class ModifierTier {
    public static final Codec<ModifierTier> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.INT.fieldOf("tier").forGetter(ModifierTier::getTier),
            AbstractModifierEffect.DIRECT_CODEC.listOf().fieldOf("modifiers").forGetter(ModifierTier::getModifierEffects)
    ).apply(inst, ModifierTier::new));

    private final int tier;
    private final List<AbstractModifierEffect> modifierEffects;

    public ModifierTier(int tier, List<AbstractModifierEffect> modifierEffects) {
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

    public List<TooltipComponent> getTooltipComponent(
            ItemStack stack,
            float roll,
            ModifierInstance instance,
            ChatFormatting chatFormatting) {
        List<TooltipComponent> tooltipComponents = new ArrayList<>();
        for (AbstractModifierEffect effect : modifierEffects) {
            tooltipComponents.add(effect.getTooltipComponent(stack, roll, chatFormatting));
        }
        return tooltipComponents;
    }
}