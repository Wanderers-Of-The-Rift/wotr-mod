package com.wanderersoftherift.wotr.abilities.effects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import com.wanderersoftherift.wotr.abilities.targeting.TargetInfo;
import com.wanderersoftherift.wotr.init.WotrDataComponentType;
import com.wanderersoftherift.wotr.modifier.effect.ModifierEffect;
import com.wanderersoftherift.wotr.modifier.effect.ProvideAbilityConditionModifierEffect;
import net.minecraft.resources.ResourceLocation;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Effect that applies different sub-effects based a condition being present.
 */
public class ConditionalEffect implements AbilityEffect {
    public static final MapCodec<ConditionalEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            AbilityEffect.DIRECT_CODEC.listOf()
                    .optionalFieldOf("always", Collections.emptyList())
                    .forGetter(ConditionalEffect::effectsAlways),
            AbilityEffect.DIRECT_CODEC.listOf()
                    .optionalFieldOf("present", Collections.emptyList())
                    .forGetter(ConditionalEffect::effectsTrue),
            AbilityEffect.DIRECT_CODEC.listOf()
                    .optionalFieldOf("missing", Collections.emptyList())
                    .forGetter(ConditionalEffect::effectsFalse),
            ResourceLocation.CODEC.fieldOf("condition_name").forGetter(ConditionalEffect::condition)
    ).apply(instance, ConditionalEffect::new));

    private final List<AbilityEffect> effectsAlways;
    private final List<AbilityEffect> effectsTrue;
    private final List<AbilityEffect> effectsFalse;
    private final ResourceLocation condition;

    public ConditionalEffect(List<AbilityEffect> effectsAlways, List<AbilityEffect> effectsTrue,
            List<AbilityEffect> effectsFalse, ResourceLocation condition) {
        this.effectsAlways = effectsAlways;
        this.effectsTrue = effectsTrue;
        this.effectsFalse = effectsFalse;
        this.condition = condition;
    }

    private List<AbilityEffect> effectsTrue() {
        return effectsTrue;
    }

    private List<AbilityEffect> effectsFalse() {
        return effectsFalse;
    }

    private List<AbilityEffect> effectsAlways() {
        return effectsAlways;
    }

    private ResourceLocation condition() {
        return condition;
    }

    @Override
    public MapCodec<? extends AbilityEffect> getCodec() {
        return CODEC;
    }

    @Override
    public void apply(AbilityContext context, TargetInfo targetInfo) {
        for (AbilityEffect effect : effectsAlways) {
            effect.apply(context, targetInfo);
        }

        var condition = context.getOrDefault(WotrDataComponentType.AbilityContextData.CONDITIONS, Set.of())
                .contains(condition());

        for (AbilityEffect effect : (condition ? effectsTrue() : effectsFalse())) {
            effect.apply(context, targetInfo);
        }
    }

    @Override
    public boolean isRelevant(ModifierEffect modifierEffect) {
        if (modifierEffect instanceof ProvideAbilityConditionModifierEffect conditionModifier
                && conditionModifier.condition().equals(condition)) {
            return true;
        }
        Predicate<AbilityEffect> relevanceCheck = (effect) -> effect.isRelevant(modifierEffect);
        return effectsAlways.stream().anyMatch(relevanceCheck) || effectsTrue.stream().anyMatch(relevanceCheck)
                || effectsFalse.stream().anyMatch(relevanceCheck);
    }
}
