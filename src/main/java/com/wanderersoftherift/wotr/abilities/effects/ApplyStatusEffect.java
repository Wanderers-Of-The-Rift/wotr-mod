package com.wanderersoftherift.wotr.abilities.effects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import com.wanderersoftherift.wotr.abilities.targeting.TargetInfo;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

/**
 * Applies a MobEffect to target entities
 */
public record ApplyStatusEffect(MobEffectInstance statusEffect) implements AbilityEffect {

    public static final MapCodec<ApplyStatusEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            MobEffectInstance.CODEC.fieldOf("status_effect").forGetter(ApplyStatusEffect::statusEffect)
    ).apply(instance, ApplyStatusEffect::new));

    @Override
    public MapCodec<? extends AbilityEffect> getCodec() {
        return CODEC;
    }

    @Override
    public void apply(AbilityContext context, TargetInfo targetInfo) {
        targetInfo.targetEntities()
                .filter(LivingEntity.class::isInstance)
                .map(LivingEntity.class::cast)
                .forEach(target -> target.addEffect(new MobEffectInstance(statusEffect())));
    }
}
