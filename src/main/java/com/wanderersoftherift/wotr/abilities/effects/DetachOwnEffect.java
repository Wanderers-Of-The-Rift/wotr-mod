package com.wanderersoftherift.wotr.abilities.effects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import com.wanderersoftherift.wotr.abilities.attachment.AttachedEffects;
import com.wanderersoftherift.wotr.abilities.targeting.TargetInfo;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

/**
 * Removed attach effects created by this ability
 */
public record DetachOwnEffect(Optional<ResourceLocation> id) implements AbilityEffect {
    public static final MapCodec<DetachOwnEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.optionalFieldOf("id").forGetter(DetachOwnEffect::id)
    ).apply(instance, DetachOwnEffect::new));

    @Override
    public void apply(AbilityContext context, TargetInfo targetInfo) {
        targetInfo.targetEntities().forEach(target -> {
            AttachedEffects attachedEffects = target.getData(WotrAttachments.ATTACHED_EFFECTS);
            if (id.isPresent()) {
                attachedEffects.detach(context.instanceId(), effect -> effect.id().equals(id));
            } else {
                attachedEffects.detach(context.instanceId());
            }
        });
    }

    @Override
    public MapCodec<? extends AbilityEffect> getCodec() {
        return CODEC;
    }

}
