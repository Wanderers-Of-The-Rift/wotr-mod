package com.wanderersoftherift.wotr.abilities.effects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import com.wanderersoftherift.wotr.abilities.effects.util.ParticleInfo;
import com.wanderersoftherift.wotr.abilities.targeting.AbilityTargeting;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;
import java.util.Optional;

public class ApplyStatusEffect extends AbilityEffect {

    public static final MapCodec<ApplyStatusEffect> CODEC = RecordCodecBuilder
            .mapCodec(instance -> AbilityEffect.commonFields(instance)
                    .and(MobEffectInstance.CODEC.fieldOf("status_effect").forGetter(ApplyStatusEffect::getStatusEffect))
                    .apply(instance, ApplyStatusEffect::new));

    private MobEffectInstance statusEffect;

    public ApplyStatusEffect(AbilityTargeting targeting, List<AbilityEffect> effects, Optional<ParticleInfo> particles,
            MobEffectInstance status) {
        super(targeting, effects, particles);
        this.statusEffect = status;
    }

    public MobEffectInstance getStatusEffect() {
        return this.statusEffect;
    }

    @Override
    public MapCodec<? extends AbilityEffect> getCodec() {
        return CODEC;
    }

    @Override
    public void apply(Entity user, List<BlockPos> blocks, AbilityContext context) {
        List<Entity> targets = getTargeting().getTargets(user, blocks, context);
        applyParticlesToUser(user);

        for (Entity target : targets) {
            applyParticlesToTarget(target);
            if (target instanceof LivingEntity livingTarget) {
                // TODO look into creating our own mob effect wrapper that can also call an effect list
                livingTarget.addEffect(new MobEffectInstance(getStatusEffect()));
            }

            // Then apply children effects to targets
            super.apply(target, getTargeting().getBlocks(user), context);
        }

        if (targets.isEmpty()) {
            super.apply(null, getTargeting().getBlocks(user), context);
        }
    }
}
