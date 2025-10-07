package com.wanderersoftherift.wotr.abilities.effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import com.wanderersoftherift.wotr.abilities.EffectMarker;
import com.wanderersoftherift.wotr.abilities.effects.predicate.ContinueEffectPredicate;
import com.wanderersoftherift.wotr.abilities.effects.predicate.TriggerPredicate;
import com.wanderersoftherift.wotr.abilities.targeting.TargetInfo;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.modifier.ModifierInstance;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Optional;

/**
 * AttachEffect attaches all of its child effects to each target entity, with a durationTicks
 */
public record AttachEffect(Optional<ResourceLocation> id, List<AbilityEffect> effects,
        TriggerPredicate triggerPredicate, ContinueEffectPredicate continuePredicate,
        Optional<Holder<EffectMarker>> display, List<ModifierInstance> modifiers) implements AbilityEffect {

    public static final MapCodec<AttachEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.optionalFieldOf("id").forGetter(AttachEffect::id),
            Codec.list(AbilityEffect.DIRECT_CODEC)
                    .optionalFieldOf("effects", List.of())
                    .forGetter(AttachEffect::effects),
            TriggerPredicate.CODEC.optionalFieldOf("trigger", new TriggerPredicate())
                    .forGetter(AttachEffect::triggerPredicate),
            ContinueEffectPredicate.CODEC.optionalFieldOf("continue", new ContinueEffectPredicate())
                    .forGetter(AttachEffect::continuePredicate),
            RegistryFixedCodec.create(WotrRegistries.Keys.EFFECT_MARKERS)
                    .optionalFieldOf("display")
                    .forGetter(AttachEffect::display),
            ModifierInstance.CODEC.listOf().optionalFieldOf("modifiers", List.of()).forGetter(AttachEffect::modifiers))
            .apply(instance, AttachEffect::new));

    @Override
    public void apply(AbilityContext context, TargetInfo targetInfo) {
        targetInfo.targetEntities()
                .forEach(target -> target.getData(WotrAttachments.ATTACHED_EFFECTS).attach(this, context));
    }

    @Override
    public MapCodec<? extends AbilityEffect> getCodec() {
        return CODEC;
    }

}
