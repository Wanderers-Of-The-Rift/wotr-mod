package com.wanderersoftherift.wotr.modifier.effect;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.Ability;
import com.wanderersoftherift.wotr.abilities.attachment.AbilityConditions;
import com.wanderersoftherift.wotr.client.tooltip.ImageComponent;
import com.wanderersoftherift.wotr.modifier.source.ModifierSource;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record ProvideAbilityConditionModifierEffect(Holder<Ability> ability, ResourceLocation condition)
        implements ModifierEffect {

    public static final MapCodec<ProvideAbilityConditionModifierEffect> MODIFIER_CODEC = RecordCodecBuilder
            .mapCodec(instance -> instance.group(
                    Ability.CODEC.fieldOf("ability").forGetter(ProvideAbilityConditionModifierEffect::ability),
                    ResourceLocation.CODEC.fieldOf("condition")
                            .forGetter(ProvideAbilityConditionModifierEffect::condition)
            ).apply(instance, ProvideAbilityConditionModifierEffect::new));

    @Override
    public MapCodec<? extends ModifierEffect> getCodec() {
        return MODIFIER_CODEC;
    }

    @Override
    public void enableModifier(double roll, Entity entity, ModifierSource source, int effectIndex) {
        AbilityConditions.forEntity(entity).addCondition(ability, condition);
    }

    @Override
    public void disableModifier(double roll, Entity entity, ModifierSource source, int effectIndex) {
        AbilityConditions.forEntity(entity).removeCondition(ability, condition);
    }

    @Override
    public List<ImageComponent> getAdvancedTooltipComponent(ItemStack stack, float roll, Style style, int tier) {
        return List.of(new ImageComponent(
                Component.translatable(condition().toLanguageKey("ability_conditions")).append(" (T" + tier + ")"),
                null));
    }

    @Override
    public List<ImageComponent> getTooltipComponent(ItemStack stack, float roll, Style style) {
        return List.of(new ImageComponent(
                Component.translatable(condition().toLanguageKey("ability_conditions")), null));
    }
}
