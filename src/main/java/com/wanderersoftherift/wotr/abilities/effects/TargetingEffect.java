package com.wanderersoftherift.wotr.abilities.effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import com.wanderersoftherift.wotr.abilities.targeting.AbilityTargeting;
import com.wanderersoftherift.wotr.abilities.targeting.TargetInfo;
import com.wanderersoftherift.wotr.modifier.effect.ModifierEffect;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;

import java.util.List;
import java.util.Set;

/**
 * Effect that applies a number of targeting steps to produce a new set of targets, before applying additional effects
 * to those targets.
 * 
 * @param targetingSteps The targeting steps to apply
 * @param effects        The effects to apply to the resulting targets
 */
public record TargetingEffect(List<AbilityTargeting> targetingSteps, List<AbilityEffect> effects)
        implements AbilityEffect {

    public static final MapCodec<TargetingEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.withAlternative(AbilityTargeting.DIRECT_CODEC.listOf(),
                    AbilityTargeting.DIRECT_CODEC.xmap(List::of, List::getFirst))
                    .fieldOf("targeting")
                    .forGetter(TargetingEffect::targetingSteps),
            Codec.list(AbilityEffect.DIRECT_CODEC).fieldOf("effects").forGetter(TargetingEffect::effects)
    ).apply(instance, TargetingEffect::new));

    @Override
    public MapCodec<? extends AbilityEffect> getCodec() {
        return CODEC;
    }

    @Override
    public void apply(AbilityContext context, TargetInfo targets) {
        List<TargetInfo> newTargets = List.of(targets);
        for (AbilityTargeting targetStep : targetingSteps) {
            newTargets = newTargets.stream()
                    .flatMap(targetInfo -> targetStep.getTargets(context, targetInfo).stream())
                    .toList();
        }

        for (AbilityEffect effect : effects) {
            for (TargetInfo newTarget : newTargets) {
                effect.apply(context, newTarget);
            }
        }
    }

    @Override
    public Set<Holder<Attribute>> getApplicableAttributes() {
        return AbilityEffect.getApplicableAttributes(effects);
    }

    @Override
    public boolean isRelevant(ModifierEffect modifierEffect) {
        if (targetingSteps().stream().anyMatch(step -> step.isRelevant(modifierEffect))) {
            return true;
        }
        for (AbilityEffect child : effects) {
            if (child.isRelevant(modifierEffect)) {
                return true;
            }
        }
        return false;
    }
}
