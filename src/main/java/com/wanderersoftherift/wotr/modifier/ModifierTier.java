package com.wanderersoftherift.wotr.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.modifier.effect.AbstractModifierEffect;
import com.wanderersoftherift.wotr.modifier.source.ModifierSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ModifierTier {
    public static final Codec<ModifierTier> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.INT.fieldOf("tier").forGetter(ModifierTier::getTier),
            AbstractModifierEffect.DIRECT_CODEC.listOf()
                    .fieldOf("modifiers")
                    .forGetter(ModifierTier::getModifierEffects)
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
        for (int i = 0; i < modifierEffects.size(); i++) {
            AbstractModifierEffect effect = modifierEffects.get(i);
            effect.enableModifier(roll, entity, source, i);
        }
    }

    public void disableModifier(float roll, Entity entity, ModifierSource source) {
        for (int i = 0; i < modifierEffects.size(); i++) {
            AbstractModifierEffect effect = modifierEffects.get(i);
            effect.disableModifier(roll, entity, source, i);
        }
    }

    public List<TooltipComponent> getTooltipComponent(ItemStack stack, float roll, ModifierInstance instance) {
        List<TooltipComponent> tooltipComponents = new ArrayList<>();
        for (AbstractModifierEffect effect : modifierEffects) {
            tooltipComponents.addAll(effect.getTooltipComponent(stack, roll, instance.modifier().value().getStyle()));
        }
        return tooltipComponents;
    }
}