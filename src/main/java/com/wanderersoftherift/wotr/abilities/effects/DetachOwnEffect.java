package com.wanderersoftherift.wotr.abilities.effects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import com.wanderersoftherift.wotr.abilities.attachment.AttachedEffects;
import com.wanderersoftherift.wotr.abilities.effects.util.ParticleInfo;
import com.wanderersoftherift.wotr.abilities.targeting.AbilityTargeting;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import java.util.List;
import java.util.Optional;

/**
 * Removed attach effects created by this ability
 */
public class DetachOwnEffect extends AbilityEffect {
    public static final MapCodec<DetachOwnEffect> CODEC = RecordCodecBuilder
            .mapCodec(instance -> AbilityEffect.commonFields(instance)
                    .and(
                            ResourceLocation.CODEC.optionalFieldOf("id").forGetter(DetachOwnEffect::getId)
                    )
                    .apply(instance, DetachOwnEffect::new));

    private final Optional<ResourceLocation> id;

    public DetachOwnEffect(AbilityTargeting targeting, List<AbilityEffect> effects, Optional<ParticleInfo> particles,
            Optional<ResourceLocation> id) {
        super(targeting, effects, particles);
        this.id = id;
    }

    @Override
    public void apply(Entity user, List<BlockPos> blocks, AbilityContext context) {
        List<Entity> targets = getTargeting().getTargets(user, blocks, context);

        applyParticlesToUser(user);

        for (Entity target : targets) {
            applyParticlesToTarget(target);
            AttachedEffects attachedEffects = target.getData(WotrAttachments.ATTACHED_EFFECTS);
            if (id.isPresent()) {
                attachedEffects.detach(context.instanceId(), effect -> effect.getId().equals(id));
            } else {
                attachedEffects.detach(context.instanceId());
            }
            super.apply(target, getTargeting().getBlocks(user), context);
        }

        if (targets.isEmpty()) {
            super.apply(null, getTargeting().getBlocks(user), context);
        }
    }

    public Optional<ResourceLocation> getId() {
        return id;
    }

    @Override
    public MapCodec<? extends AbilityEffect> getCodec() {
        return CODEC;
    }

}
