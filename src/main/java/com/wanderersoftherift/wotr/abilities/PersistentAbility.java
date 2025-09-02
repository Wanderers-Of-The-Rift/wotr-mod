package com.wanderersoftherift.wotr.abilities;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.attachment.AbilityStates;
import com.wanderersoftherift.wotr.abilities.effects.AbilityEffect;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.modifier.effect.ModifierEffect;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * An ability that persist until some condition fails to be met or is deactivated
 */
public class PersistentAbility implements Ability {

    public static final MapCodec<PersistentAbility> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    ResourceLocation.CODEC.fieldOf("icon").forGetter(PersistentAbility::getIcon),
                    ResourceLocation.CODEC.optionalFieldOf("small_icon").forGetter(x -> x.smallIcon),
                    Codec.INT.optionalFieldOf("warmup_time", 0).forGetter(PersistentAbility::getWarmupTime),
                    Codec.BOOL.optionalFieldOf("can_deactivate", true).forGetter(PersistentAbility::canDeactivate),
                    Codec.BOOL.optionalFieldOf("channelled", false).forGetter(PersistentAbility::isChannelled),
                    AbilityRequirement.CODEC.listOf()
                            .optionalFieldOf("requirements", List.of())
                            .forGetter(PersistentAbility::getActivationRequirements),
                    AbilityRequirement.CODEC.listOf()
                            .optionalFieldOf("ongoing_requirements", List.of())
                            .forGetter(PersistentAbility::getOngoingRequirements),
                    AbilityRequirement.CODEC.listOf()
                            .optionalFieldOf("on_deactivation_costs", List.of())
                            .forGetter(PersistentAbility::getOnDeactivationCosts),
                    Codec.list(AbilityEffect.DIRECT_CODEC)
                            .optionalFieldOf("activation_effects", Collections.emptyList())
                            .forGetter(PersistentAbility::getActivationEffects),
                    Codec.list(AbilityEffect.DIRECT_CODEC)
                            .optionalFieldOf("on_deactivation_effects", Collections.emptyList())
                            .forGetter(PersistentAbility::getDeactivationEffects)
            ).apply(instance, PersistentAbility::new));

    private static final int ACTIVE = 1;

    private final ResourceLocation icon;
    private final Optional<ResourceLocation> smallIcon;
    private final int warmupTime;
    private final boolean canDeactivate;
    private final boolean channelled;
    private final List<AbilityRequirement> activationRequirements;
    private final List<AbilityRequirement> ongoingRequirements;
    private final List<AbilityEffect> activationEffects;
    private final List<AbilityEffect> deactivationEffects;
    private final List<AbilityRequirement> onDeactivationCosts;

    public PersistentAbility(ResourceLocation icon, Optional<ResourceLocation> smallIcon, int warmupTime,
            boolean canDeactivate, boolean channelled, List<AbilityRequirement> activationRequirements,
            List<AbilityRequirement> ongoingRequirements, List<AbilityRequirement> onDeactivationCosts,
            List<AbilityEffect> activationEffects, List<AbilityEffect> deactivationEffects) {
        this.icon = icon;
        this.smallIcon = smallIcon;
        this.warmupTime = warmupTime;
        this.canDeactivate = canDeactivate;
        this.channelled = channelled;
        this.activationRequirements = ImmutableList.copyOf(activationRequirements);
        this.ongoingRequirements = ImmutableList.copyOf(ongoingRequirements);
        this.onDeactivationCosts = ImmutableList.copyOf(onDeactivationCosts);
        this.activationEffects = ImmutableList.copyOf(activationEffects);
        this.deactivationEffects = ImmutableList.copyOf(deactivationEffects);
    }

    @Override
    public ResourceLocation getIcon() {
        return icon;
    }

    @Override
    public ResourceLocation getEmblem() {
        return smallIcon.orElse(icon);
    }

    @Override
    public boolean canActivate(AbilityContext context) {
        if (context.caster().getData(WotrAttachments.ABILITY_STATES).isActive(context.source())) {
            return canDeactivate;
        } else if (context.caster().getData(WotrAttachments.ABILITY_COOLDOWNS).isOnCooldown(context.source())) {
            return false;
        } else {
            return getActivationRequirements().stream().allMatch(x -> x.check(context));
        }
    }

    @Override
    public boolean activate(AbilityContext context) {
        AbilityStates states = context.caster().getData(WotrAttachments.ABILITY_STATES);
        if (!states.isActive(context.source())) {
            states.setState(context.source(), ACTIVE);
            getActivationRequirements().forEach(x -> x.pay(context));
            if (warmupTime == 0) {
                return tick(context);
            }
            return false;
        } else if (canDeactivate) {
            deactivate(context, states);
            return true;
        }
        return false;
    }

    @Override
    public void deactivate(AbilityContext context) {
        AbilityStates states = context.caster().getData(WotrAttachments.ABILITY_STATES);
        deactivate(context, states);
    }

    @Override
    public void clientActivate(AbilityContext context) {
        context.caster().getData(WotrAttachments.ABILITY_STATES).setState(context.source(), ACTIVE);
    }

    @Override
    public boolean tick(AbilityContext context) {
        if ((context.age() - warmupTime) == 0) {
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
        states.setState(context.source(), 0);
    }

    @Override
    public boolean isRelevantModifier(ModifierEffect modifierEffect) {
        return activationEffects.stream().anyMatch(x -> x.isRelevant(modifierEffect))
                || deactivationEffects.stream().anyMatch(x -> x.isRelevant(modifierEffect))
                || onDeactivationCosts.stream().anyMatch(x -> x.isRelevant(modifierEffect))
                || activationRequirements.stream().anyMatch(x -> x.isRelevant(modifierEffect));
    }

    public boolean canDeactivate() {
        return canDeactivate;
    }

    @Override
    public boolean isChannelled() {
        return channelled;
    }

    public List<AbilityRequirement> getActivationRequirements() {
        return activationRequirements;
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
