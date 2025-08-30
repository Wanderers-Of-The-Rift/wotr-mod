package com.wanderersoftherift.wotr.abilities;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.attachment.AbilityStates;
import com.wanderersoftherift.wotr.abilities.effects.AbilityEffect;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.modifier.effect.AbstractModifierEffect;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * An ability that can be toggled on and off
 */
public class ToggleAbility extends Ability {

    public static final MapCodec<ToggleAbility> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    ResourceLocation.CODEC.fieldOf("icon").forGetter(ToggleAbility::getIcon),
                    ResourceLocation.CODEC.optionalFieldOf("small_icon").forGetter(ToggleAbility::getSmallIcon),
                    Codec.INT.optionalFieldOf("warmup_time", 0).forGetter(ToggleAbility::getWarmupTime),
                    AbilityRequirement.CODEC.listOf()
                            .optionalFieldOf("requirements", List.of())
                            .forGetter(Ability::getActivationRequirements),
                    AbilityRequirement.CODEC.listOf()
                            .optionalFieldOf("ongoing_requirements", List.of())
                            .forGetter(ToggleAbility::getOngoingRequirements),
                    AbilityRequirement.CODEC.listOf()
                            .optionalFieldOf("on_deactivation_costs", List.of())
                            .forGetter(ToggleAbility::getOnDeactivationCosts),
                    Codec.list(AbilityEffect.DIRECT_CODEC)
                            .optionalFieldOf("activation_effects", Collections.emptyList())
                            .forGetter(ToggleAbility::getActivationEffects),
                    Codec.list(AbilityEffect.DIRECT_CODEC)
                            .optionalFieldOf("on_deactivation_effects", Collections.emptyList())
                            .forGetter(ToggleAbility::getDeactivationEffects)
            ).apply(instance, ToggleAbility::new));

    private final int warmupTime;
    private final List<AbilityRequirement> ongoingRequirements;
    private final List<AbilityEffect> activationEffects;
    private final List<AbilityEffect> deactivationEffects;
    private final List<AbilityRequirement> onDeactivationCosts;

    public ToggleAbility(ResourceLocation icon, Optional<ResourceLocation> smallIcon, int warmupTime,
            List<AbilityRequirement> activationRequirements, List<AbilityRequirement> ongoingRequirements,
            List<AbilityRequirement> onDeactivationCosts, List<AbilityEffect> activationEffects,
            List<AbilityEffect> deactivationEffects) {
        super(icon, smallIcon, activationRequirements);
        this.warmupTime = warmupTime;
        this.ongoingRequirements = ongoingRequirements;
        this.onDeactivationCosts = onDeactivationCosts;
        this.activationEffects = activationEffects;
        this.deactivationEffects = deactivationEffects;
    }

    @Override
    public boolean canActivate(AbilityContext context) {
        if (context.caster().getData(WotrAttachments.ABILITY_STATES).isActive(context.source())) {
            // Can always deactivate
            return true;
        } else if (context.caster().getData(WotrAttachments.ABILITY_COOLDOWNS).isOnCooldown(context.source())) {
            return false;
        } else {
            return getActivationRequirements().stream().allMatch(x -> x.check(context));
        }
    }

    @Override
    public boolean activate(AbilityContext context) {
        AbilityStates states = context.caster().getData(WotrAttachments.ABILITY_STATES);
        if (states.isActive(context.source())) {
            deactivate(context, states);
            return true;
        } else {
            states.setActive(context.source(), true);
            getActivationRequirements().forEach(x -> x.pay(context));
            if (warmupTime == 0) {
                return tick(context, 0);
            }
            return false;
        }
    }

    @Override
    public void clientActivate(AbilityContext context) {
        context.caster().getData(WotrAttachments.ABILITY_STATES).setActive(context.source(), true);
    }

    @Override
    public boolean tick(AbilityContext context, long age) {
        if ((age - warmupTime) == 0) {
            activationEffects.forEach(effect -> effect.apply(context.caster(), new ArrayList<>(), context));
        }
        if (!ongoingRequirements.isEmpty() && ongoingRequirements.stream().anyMatch(x -> !x.check(context))) {
            deactivate(context, context.caster().getData(WotrAttachments.ABILITY_STATES));
            return true;
        }
        return false;
    }

    private void deactivate(AbilityContext context, AbilityStates states) {
        deactivationEffects.forEach(effect -> effect.apply(context.caster(), new ArrayList<>(), context));
        onDeactivationCosts.forEach(cost -> cost.pay(context));
        states.setActive(context.source(), false);
    }

    @Override
    public boolean isRelevantModifier(AbstractModifierEffect modifierEffect) {
        return activationEffects.stream().anyMatch(x -> x.isRelevant(modifierEffect))
                || deactivationEffects.stream().anyMatch(x -> x.isRelevant(modifierEffect))
                || onDeactivationCosts.stream().anyMatch(x -> x.isRelevant(modifierEffect))
                || super.isRelevantModifier(modifierEffect);
    }

    public List<AbilityRequirement> getOngoingRequirements() {
        return ongoingRequirements;
    }

    public List<AbilityRequirement> getOnDeactivationCosts() {
        return onDeactivationCosts;
    }

    public int getWarmupTime() {
        return warmupTime;
    }

    public List<AbilityEffect> getActivationEffects() {
        return activationEffects;
    }

    public List<AbilityEffect> getDeactivationEffects() {
        return deactivationEffects;
    }

    @Override
    public MapCodec<? extends Ability> getCodec() {
        return CODEC;
    }
}
