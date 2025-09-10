package com.wanderersoftherift.wotr.abilities.effects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import com.wanderersoftherift.wotr.abilities.attachment.AttachedEffects;
import com.wanderersoftherift.wotr.abilities.targeting.TargetInfo;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.EntityHitResult;

import java.util.Optional;

/**
 * Removed attach effects created by this ability
 */
public class DetachOwnEffect implements AbilityEffect {
    public static final MapCodec<DetachOwnEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.optionalFieldOf("id").forGetter(DetachOwnEffect::getId)
    ).apply(instance, DetachOwnEffect::new));

    private final Optional<ResourceLocation> id;

    public DetachOwnEffect(Optional<ResourceLocation> id) {
        this.id = id;
    }

    @Override
    public void apply(AbilityContext context, TargetInfo targetInfo) {
        targetInfo.targetEntities().map(EntityHitResult::getEntity).forEach(target -> {
            AttachedEffects attachedEffects = target.getData(WotrAttachments.ATTACHED_EFFECTS);
            if (id.isPresent()) {
                attachedEffects.detach(context.instanceId(), effect -> effect.getId().equals(id));
            } else {
                attachedEffects.detach(context.instanceId());
            }
        });
    }

    public Optional<ResourceLocation> getId() {
        return id;
    }

    @Override
    public MapCodec<? extends AbilityEffect> getCodec() {
        return CODEC;
    }

}
