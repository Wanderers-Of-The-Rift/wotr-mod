package com.wanderersoftherift.wotr.abilities.effects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import com.wanderersoftherift.wotr.abilities.effects.util.ParticleInfo;
import com.wanderersoftherift.wotr.abilities.targeting.AbilityTargeting;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;

import java.util.List;
import java.util.Optional;

public class BlankEffect extends AbilityEffect {

    // TODO setup healing amount as part of the codec
    public static final MapCodec<BlankEffect> CODEC = RecordCodecBuilder
            .mapCodec(instance -> AbilityEffect.commonFields(instance).apply(instance, BlankEffect::new));

    public BlankEffect(AbilityTargeting targeting, List<AbilityEffect> effects, Optional<ParticleInfo> particles) {
        super(targeting, effects, particles);
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
            // Then apply children affects to targets
            super.apply(target, getTargeting().getBlocks(user), context);
        }

        if (targets.isEmpty()) {
            super.apply(null, getTargeting().getBlocks(user), context);
        }
    }
}
