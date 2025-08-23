package com.wanderersoftherift.wotr.item.ability;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.Ability;
import com.wanderersoftherift.wotr.abilities.TrackedAbilityTrigger;
import com.wanderersoftherift.wotr.abilities.attachment.AbilityTracker;
import com.wanderersoftherift.wotr.client.tooltip.ImageComponent;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.modifier.effect.AbstractModifierEffect;
import com.wanderersoftherift.wotr.modifier.source.ModifierSource;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

public final class AbilityModifier extends AbstractModifierEffect {

    public static final MapCodec<AbilityModifier> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance
                    .group(Ability.CODEC.fieldOf("provided_ability").forGetter(AbilityModifier::providedAbility),
                            WotrRegistries.TRACKED_ABILITY_TRIGGERS.holderByNameCodec()
                                    .fieldOf("trigger")
                                    .forGetter(AbilityModifier::trigger)
                    ).apply(instance, AbilityModifier::new));

    private final Holder<Ability> providedAbility;
    private final Holder<TrackedAbilityTrigger.Type<?>> trigger;

    public AbilityModifier(Holder<Ability> providedAbility, Holder<TrackedAbilityTrigger.Type<?>> trigger) {
        this.providedAbility = providedAbility;
        this.trigger = trigger;
    }

    @Override
    public MapCodec<? extends AbstractModifierEffect> getCodec() {
        return CODEC;
    }

    @Override
    public void enableModifier(double roll, Entity entity, ModifierSource source) {
        AbilityTracker.forEntity(entity).registerAbility(this, source.slot());
    }

    @Override
    public void disableModifier(double roll, Entity entity, ModifierSource source) {
        AbilityTracker.forEntity(entity).unregisterAbility(this, source.slot());
    }

    @Override
    public void applyModifier() {
        // what is this unused?
    }

    @Override
    public TooltipComponent getTooltipComponent(ItemStack stack, float roll, Style style) {
        var text = Component.literal("")
                .append(Component.translatable(
                        WanderersOfTheRift.translationId("ability", providedAbility().getKey().location())))
                .append(" when ")
                .append(Component
                        .translatable(WanderersOfTheRift.translationId("trigger", trigger().getKey().location())));

        return new ImageComponent(stack, text,
                WanderersOfTheRift.id("textures/tooltip/attribute/damage_attribute.png"));
    }

    public Holder<Ability> providedAbility() {
        return providedAbility;
    }

    public Holder<TrackedAbilityTrigger.Type<?>> trigger() {
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

}
