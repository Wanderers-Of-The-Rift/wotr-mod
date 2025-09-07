package com.wanderersoftherift.wotr.modifier.effect;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.Ability;
import com.wanderersoftherift.wotr.abilities.attachment.AbilityEnhancements;
import com.wanderersoftherift.wotr.client.tooltip.ImageComponent;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.modifier.Modifier;
import com.wanderersoftherift.wotr.modifier.ModifierInstance;
import com.wanderersoftherift.wotr.modifier.source.ModifierSource;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

public class EnhanceAbilityModifierEffect extends AbstractModifierEffect {
    public static final MapCodec<EnhanceAbilityModifierEffect> MODIFIER_CODEC = RecordCodecBuilder
            .mapCodec(instance -> instance.group(
                    RegistryCodecs.homogeneousList(WotrRegistries.Keys.ABILITIES)
                            .fieldOf("ability")
                            .forGetter(EnhanceAbilityModifierEffect::abilities),
                    Modifier.CODEC.fieldOf("modifier").forGetter(EnhanceAbilityModifierEffect::modifier),
                    Codec.INT.fieldOf("tier").forGetter(EnhanceAbilityModifierEffect::tier)
            ).apply(instance, EnhanceAbilityModifierEffect::new));

    private final HolderSet<Ability> abilities;
    private final Holder<Modifier> modifier;
    private final int tier;

    public EnhanceAbilityModifierEffect(HolderSet<Ability> abilities, Holder<Modifier> modifier, int tier) {
        this.abilities = abilities;
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
    public List<ImageComponent> getAdvancedTooltipComponent(ItemStack stack, float roll, Style style, int tier) {
        var abilityTextComponent = abilities.stream()
                .map(Holder::unwrapKey)
                .filter(Optional::isPresent)
                .map(it -> WanderersOfTheRift.translationId("ability", it.get().location()))
                .map(Component::translatable)
                .reduce((a, b) -> a.append(", ").append(b));
        var result = ImmutableList.<ImageComponent>builder();
        result.add(new ImageComponent(stack,
                Component.literal("for ")
                        .append(abilityTextComponent
                                .orElse(Component.literal("nothing").withStyle(Style.EMPTY.withItalic(true))))
                        .append(Component.literal(": [").withStyle(style)),
                null));
        result.addAll(modifier.value()
                .getAdvancedTooltipComponent(stack, roll, new ModifierInstance(modifier, this.tier, roll)));
        result.add(new ImageComponent(stack, Component.literal("] (T" + tier + ")").withStyle(style), null));
        return result.build();
    }

    @Override
    public List<ImageComponent> getTooltipComponent(ItemStack stack, float roll, Style style) {
        var abilityTextComponent = abilities.stream()
                .map(Holder::unwrapKey)
                .filter(Optional::isPresent)
                .map(it -> WanderersOfTheRift.translationId("ability", it.get().location()))
                .map(Component::translatable)
                .reduce((a, b) -> a.append(", ").append(b));
        var result = ImmutableList.<ImageComponent>builder();
        result.add(new ImageComponent(stack,
                Component.literal("for ")
                        .append(abilityTextComponent
                                .orElse(Component.literal("nothing").withStyle(Style.EMPTY.withItalic(true))))
                        .append(Component.literal(": ["))
                        .withStyle(style),
                null));
        result.addAll(modifier.value().getTooltipComponent(stack, roll, new ModifierInstance(modifier, tier, roll)));
        result.add(new ImageComponent(stack, Component.literal("]").withStyle(style), null));
        return result.build();
    }

    public HolderSet<Ability> abilities() {
        return abilities;
    }

    public Holder<Modifier> modifier() {
        return modifier;
    }

    public int tier() {
        return tier;
    }
}
