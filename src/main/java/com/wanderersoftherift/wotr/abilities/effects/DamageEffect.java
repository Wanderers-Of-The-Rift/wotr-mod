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
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;
import java.util.Optional;

public class DamageEffect extends AbstractEffect {
    public static final MapCodec<DamageEffect> CODEC = RecordCodecBuilder
            .mapCodec(instance -> AbstractEffect.commonFields(instance)
                    .and(instance.group(Codec.FLOAT.fieldOf("amount").forGetter(DamageEffect::getAmount),
                            DamageType.CODEC.fieldOf("damage_type").forGetter(DamageEffect::getDamageTypeKey)))
                    .apply(instance, DamageEffect::new));

    private float damageAmount = 0;
    private final Holder<DamageType> damageTypeKey;

    public DamageEffect(AbstractTargeting targeting, List<AbstractEffect> effects, Optional<ParticleInfo> particles,
            float amount, Holder<DamageType> damageTypeKey) {
        super(targeting, effects, particles);
        this.damageAmount = amount;
        this.damageTypeKey = damageTypeKey;
    }

    private Holder<DamageType> getDamageTypeKey() {
        return damageTypeKey;
    }

    private float getAmount() {
        return damageAmount;
    }

    @Override
    public MapCodec<? extends AbstractEffect> getCodec() {
        return CODEC;
    }

    @Override
    public void apply(Entity user, List<BlockPos> blocks, AbilityContext context) {
        List<Entity> targets = getTargeting().getTargets(user, blocks, context);
        DamageSource damageSource = new DamageSource(context.level()
                .registryAccess()
                .lookupOrThrow(Registries.DAMAGE_TYPE)
                .getOrThrow(this.damageTypeKey.getKey()), null, context.caster(), null);

        applyParticlesToUser(user);

        // for now its ABILITY_DAMAGE but needs to be considered how multiple types are going to be implemented ie AP or
        // AD
        float finalDamage = context.getAbilityAttribute(ModAttributes.ABILITY_DAMAGE, damageAmount);

        for (Entity target : targets) {
            applyParticlesToTarget(target);
            if (target instanceof LivingEntity livingTarget) {
                livingTarget.hurtServer((ServerLevel) target.level(), damageSource, finalDamage);
            }
            // Then apply children affects to targets
            super.apply(target, getTargeting().getBlocks(user), context);
        }

        if (targets.isEmpty()) {
            super.apply(null, getTargeting().getBlocks(user), context);
        }
    }

    @Override
    public boolean isRelevantToThis(AbstractModifierEffect modifierEffect) {
        return modifierEffect instanceof AttributeModifierEffect attributeModifierEffect
                && ModAttributes.ABILITY_DAMAGE.equals(attributeModifierEffect.getAttribute());
    }
}
