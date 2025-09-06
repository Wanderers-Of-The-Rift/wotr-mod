package com.wanderersoftherift.wotr.modifier.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.Ability;
import com.wanderersoftherift.wotr.abilities.attachment.AbilityEnhancements;
import com.wanderersoftherift.wotr.client.tooltip.ImageComponent;
import com.wanderersoftherift.wotr.modifier.Modifier;
import com.wanderersoftherift.wotr.modifier.ModifierInstance;
import com.wanderersoftherift.wotr.modifier.source.ModifierSource;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

public class EnhanceAbilityModifierEffect extends AbstractModifierEffect {
    public static final MapCodec<EnhanceAbilityModifierEffect> MODIFIER_CODEC = RecordCodecBuilder
            .mapCodec(instance -> instance.group(
                    Ability.CODEC.fieldOf("ability").forGetter(EnhanceAbilityModifierEffect::ability),
                    Modifier.CODEC.fieldOf("modifier").forGetter(EnhanceAbilityModifierEffect::modifier),
                    Codec.INT.fieldOf("tier").forGetter(EnhanceAbilityModifierEffect::tier)
            ).apply(instance, EnhanceAbilityModifierEffect::new));

    private final Holder<Ability> ability;
    private final Holder<Modifier> modifier;
    private final int tier;

    public EnhanceAbilityModifierEffect(Holder<Ability> ability, Holder<Modifier> modifier, int tier) {
        this.ability = ability;
        this.modifier = modifier;
        this.tier = tier;
    }

    @Override
    public MapCodec<? extends AbstractModifierEffect> getCodec() {
        return MODIFIER_CODEC;
    }

    @Override
    public void enableModifier(double roll, Entity entity, ModifierSource source, int effectIndex) {
        AbilityEnhancements.forEntity(entity).putEnhancement(source, effectIndex, this, roll);
    }

    @Override
    public void disableModifier(double roll, Entity entity, ModifierSource source, int effectIndex) {
        AbilityEnhancements.forEntity(entity).removeEnhancement(source, effectIndex, this);
    }

    @Override
    public TooltipComponent getTooltipComponent(ItemStack stack, float roll, Style style) {
        var baseComponent = modifier.value()
                .getTooltipComponent(stack, roll, new ModifierInstance(modifier, tier, roll))
                .getFirst(); // any other ideas?
        if (baseComponent instanceof ImageComponent image) {
            var abilityTextComponent = Component
                    .translatable(WanderersOfTheRift.translationId("ability", ability.unwrapKey().get().location()));
            return new ImageComponent(image.stack(),
                    Component.literal("for ")
                            .append(abilityTextComponent)
                            .append(Component.literal(": "))
                            .append(image.base()),
                    image.asset());
        }
        return baseComponent;
    }

    public Holder<Ability> ability() {
        return ability;
    }

    public Holder<Modifier> modifier() {
        return modifier;
    }

    public int tier() {
        return tier;
    }
}
