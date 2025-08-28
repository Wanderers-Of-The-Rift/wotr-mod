package com.wanderersoftherift.wotr.abilities.effects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import com.wanderersoftherift.wotr.abilities.effects.util.ParticleInfo;
import com.wanderersoftherift.wotr.abilities.targeting.AbilityTargeting;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;

import java.util.List;
import java.util.Optional;

/**
 * Removed attach effects created by this ability
 */
public class DetachOwnEffect extends AbilityEffect {
    public static final MapCodec<DetachOwnEffect> CODEC = RecordCodecBuilder
            .mapCodec(instance -> AbilityEffect.commonFields(instance).apply(instance, DetachOwnEffect::new));

    public DetachOwnEffect(AbilityTargeting targeting, List<AbilityEffect> effects, Optional<ParticleInfo> particles) {
        super(targeting, effects, particles);
    }

    @Override
    public void apply(Entity user, List<BlockPos> blocks, AbilityContext context) {
        List<Entity> targets = getTargeting().getTargets(user, blocks, context);

        applyParticlesToUser(user);

        for (Entity target : targets) {
            applyParticlesToTarget(target);
            target.getData(WotrAttachments.ATTACHED_EFFECTS).detach(context.instanceId());
            super.apply(target, getTargeting().getBlocks(user), context);
        }

        if (targets.isEmpty()) {
            super.apply(null, getTargeting().getBlocks(user), context);
        }
    }

    @Override
    public MapCodec<? extends AbilityEffect> getCodec() {
        return CODEC;
    }

}
