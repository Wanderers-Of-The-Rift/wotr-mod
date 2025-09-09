package com.wanderersoftherift.wotr.item.ability;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.Ability;
import com.wanderersoftherift.wotr.abilities.TrackedAbilityTrigger;
import com.wanderersoftherift.wotr.abilities.attachment.AbilityTracker;
import com.wanderersoftherift.wotr.abilities.sources.AbilitySource;
import com.wanderersoftherift.wotr.client.tooltip.ImageComponent;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.modifier.effect.ModifierEffect;
import com.wanderersoftherift.wotr.modifier.source.ModifierSource;
import com.wanderersoftherift.wotr.util.ComponentUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Objects;

public final class AbilityModifier implements ModifierEffect {

    public static final MapCodec<AbilityModifier> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance
                    .group(Ability.CODEC.fieldOf("provided_ability").forGetter(AbilityModifier::providedAbility),
                            WotrRegistries.TRACKED_ABILITY_TRIGGERS.holderByNameCodec()
                                    .fieldOf("trigger")
                                    .forGetter(AbilityModifier::trigger)
                    ).apply(instance, AbilityModifier::new));

    private final Holder<Ability> providedAbility;
    private final Holder<TrackedAbilityTrigger.TriggerType<?>> trigger;

    public AbilityModifier(Holder<Ability> providedAbility, Holder<TrackedAbilityTrigger.TriggerType<?>> trigger) {
        this.providedAbility = providedAbility;
        this.trigger = trigger;
    }

    @Override
    public MapCodec<? extends ModifierEffect> getCodec() {
        return CODEC;
    }

    @Override
    public void enableModifier(double roll, Entity entity, ModifierSource source, int effectIndex) {
        AbilityTracker.forEntity(entity).registerAbility(this, AbilitySource.byModifierSource(source, effectIndex));
    }

    @Override
    public void disableModifier(double roll, Entity entity, ModifierSource source, int effectIndex) {
        AbilityTracker.forEntity(entity).unregisterAbility(this, AbilitySource.byModifierSource(source, effectIndex));
    }

    @Override
    public List<ImageComponent> getAdvancedTooltipComponent(ItemStack stack, float roll, Style style, int tier) {
        var base = getTooltipComponent(stack, roll, style);
        return base.stream()
                .map(
                        it -> new ImageComponent(it.stack(),
                                ComponentUtil.mutable(it.base()).append(getTierInfoString(tier)), it.asset())
                )
                .toList();
    }

    @Override
    public List<ImageComponent> getTooltipComponent(ItemStack stack, float roll, Style style) {
        var text = Component.translatable(
                WanderersOfTheRift.translationId("modifier_effect", "ability"), Component.translatable(
                        WanderersOfTheRift.translationId("ability", providedAbility().getKey().location())),
                Component.translatable(WanderersOfTheRift.translationId("trigger", trigger().getKey().location()))
        );

        return List.of(new ImageComponent(stack, text.withStyle(style),
                providedAbility.value().getSmallIcon().orElse(providedAbility.value().getIcon())));
    }

    public Holder<Ability> providedAbility() {
        return providedAbility;
    }

    public Holder<TrackedAbilityTrigger.TriggerType<?>> trigger() {
        return trigger;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        var that = (AbilityModifier) obj;
        return Objects.equals(this.providedAbility, that.providedAbility) && Objects.equals(this.trigger, that.trigger);
    }

    @Override
    public int hashCode() {
        return Objects.hash(providedAbility, trigger);
    }

    @Override
    public String toString() {
        return "AbilityModifier[" + "providedAbility=" + providedAbility + ", " + "trigger=" + trigger + ']';
    }

    private String getTierInfoString(int tier) {
        return " (T%d)".formatted(tier);
    }

    private Component getTierInfo(int tier) {
        return Component.literal(getTierInfoString(tier)).withStyle(ChatFormatting.DARK_GRAY);
    }
}
