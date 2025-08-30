package com.wanderersoftherift.wotr.abilities.requirement;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import com.wanderersoftherift.wotr.abilities.AbilityRequirement;
import com.wanderersoftherift.wotr.abilities.effects.AttachEffect;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;
import java.util.function.Predicate;

public record OwnAttachedEffectRequirement(Optional<ResourceLocation> id) implements AbilityRequirement {

    public static final MapCodec<OwnAttachedEffectRequirement> CODEC = RecordCodecBuilder
            .mapCodec(instance -> instance.group(
                    ResourceLocation.CODEC.optionalFieldOf("id").forGetter(OwnAttachedEffectRequirement::id)
            ).apply(instance, OwnAttachedEffectRequirement::new));

    @Override
    public MapCodec<? extends AbilityRequirement> getCodec() {
        return CODEC;
    }

    @Override
    public boolean check(AbilityContext context) {
        Predicate<AttachEffect> predicate = id.<Predicate<AttachEffect>>map(
                present -> (effect) -> effect.getId().equals(id)).orElse((effect) -> true);
        return context.caster()
                .getExistingData(WotrAttachments.ATTACHED_EFFECTS)
                .map(effects -> effects.has(context.instanceId(), predicate))
                .orElse(false);
    }
}
