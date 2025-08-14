package com.wanderersoftherift.wotr.abilities.effects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import com.wanderersoftherift.wotr.abilities.effects.util.ParticleInfo;
import com.wanderersoftherift.wotr.abilities.targeting.AbilityTargeting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attribute;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class TargetEffect extends AbilityEffect {

    public static final MapCodec<TargetEffect> CODEC = RecordCodecBuilder
            .mapCodec(instance -> AbilityEffect.commonFields(instance).apply(instance, TargetEffect::new));

    public TargetEffect(AbilityTargeting targeting, List<AbilityEffect> effects, Optional<ParticleInfo> particles) {
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

        WanderersOfTheRift.LOGGER.info("Targetting: " + targets.size());
        for (Entity target : targets) {
            applyParticlesToTarget(target);
            // Then apply children effects to targets
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
}
