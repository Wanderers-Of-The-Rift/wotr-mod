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

public class TargetingEffect implements AbilityEffect {

    public static final MapCodec<TargetingEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            AbilityTargeting.DIRECT_CODEC.fieldOf("targeting").forGetter(TargetingEffect::getTargeting),
            Codec.list(AbilityEffect.DIRECT_CODEC).fieldOf("effects").forGetter(TargetingEffect::getEffects)
    ).apply(instance, TargetingEffect::new));

    private final AbilityTargeting targeting;
    private final List<AbilityEffect> effects;

    public TargetingEffect(AbilityTargeting targeting, List<AbilityEffect> effects) {
        this.targeting = targeting;
        this.effects = effects;
    }

    @Override
    public MapCodec<? extends AbilityEffect> getCodec() {
        return CODEC;
    }

    @Override
    public void apply(AbilityContext context, TargetInfo targets) {
        List<TargetInfo> newTargets = targeting.getTargets(context, targets);

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
        if (getTargeting().isRelevant(modifierEffect)) {
            return true;
        }
        for (AbilityEffect child : effects) {
            if (child.isRelevant(modifierEffect)) {
                return true;
            }
        }
        return false;
    }

    public AbilityTargeting getTargeting() {
        return targeting;
    }

    public List<AbilityEffect> getEffects() {
        return effects;
    }
}
