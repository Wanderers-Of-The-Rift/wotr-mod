package com.wanderersoftherift.wotr.abilities.effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import com.wanderersoftherift.wotr.abilities.effects.util.ParticleInfo;
import com.wanderersoftherift.wotr.abilities.targeting.AbstractTargeting;
import com.wanderersoftherift.wotr.init.ModAttributes;
import com.wanderersoftherift.wotr.modifier.effect.AbstractModifierEffect;
import com.wanderersoftherift.wotr.modifier.effect.AttributeModifierEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class HealEffect extends AbstractEffect {
    public static final MapCodec<HealEffect> CODEC = RecordCodecBuilder
            .mapCodec(instance -> AbstractEffect.commonFields(instance)
                    .and(Codec.FLOAT.fieldOf("amount").forGetter(HealEffect::getAmount))
                    .apply(instance, HealEffect::new));

    private float healAmount = 0;

    public HealEffect(AbstractTargeting targeting, List<AbstractEffect> effects, Optional<ParticleInfo> particles,
            float amount) {
        super(targeting, effects, particles);
        this.healAmount = amount;
    }

    public float getAmount() {
        return healAmount;
    }

    @Override
    public MapCodec<? extends AbstractEffect> getCodec() {
        return CODEC;
    }

    @Override
    public void apply(Entity user, List<BlockPos> blocks, AbilityContext context) {
        List<Entity> targets = getTargeting().getTargets(user, blocks, context);
        applyParticlesToUser(user);

        float finalHealAmount = context.getAbilityAttribute(ModAttributes.HEAL_POWER, healAmount);
        for (Entity target : targets) {
            applyParticlesToTarget(target);
            if (target instanceof LivingEntity living) {
                living.heal(finalHealAmount);
            }
            // Then apply children affects to targets
            super.apply(target, getTargeting().getBlocks(user), context);
        }

        if (targets.isEmpty()) {
            super.apply(null, getTargeting().getBlocks(user), context);
        }
    }

    @Override
    public Set<Holder<Attribute>> getApplicableAttributes() {
        return super.getApplicableAttributes();
    }

    @Override
    protected boolean isRelevantToThis(AbstractModifierEffect modifierEffect) {
        return modifierEffect instanceof AttributeModifierEffect attributeModifierEffect
                && ModAttributes.HEAL_POWER.equals(attributeModifierEffect.getAttribute());
    }
}
