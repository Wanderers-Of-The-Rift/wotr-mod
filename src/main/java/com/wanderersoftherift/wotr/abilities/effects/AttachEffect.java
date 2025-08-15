package com.wanderersoftherift.wotr.abilities.effects;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import com.wanderersoftherift.wotr.abilities.EffectMarker;
import com.wanderersoftherift.wotr.abilities.effects.predicate.ContinueEffectPredicate;
import com.wanderersoftherift.wotr.abilities.effects.predicate.TriggerPredicate;
import com.wanderersoftherift.wotr.abilities.effects.util.ParticleInfo;
import com.wanderersoftherift.wotr.abilities.targeting.AbilityTargeting;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.modifier.ModifierInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.world.entity.Entity;

import java.util.List;
import java.util.Optional;

/**
 * AttachEffect attaches all of its child effects to each target entity, with a durationTicks
 */
public class AttachEffect extends AbilityEffect {

    public static final MapCodec<AttachEffect> CODEC = RecordCodecBuilder
            .mapCodec(instance -> AbilityEffect.commonFields(instance)
                    .and(instance.group(
                            TriggerPredicate.CODEC.optionalFieldOf("trigger", new TriggerPredicate())
                                    .forGetter(AttachEffect::getTriggerPredicate),
                            ContinueEffectPredicate.CODEC.optionalFieldOf("continue", new ContinueEffectPredicate())
                                    .forGetter(AttachEffect::getContinuePredicate),
                            RegistryFixedCodec.create(WotrRegistries.Keys.EFFECT_MARKERS)
                                    .optionalFieldOf("display")
                                    .forGetter(AttachEffect::getDisplay),
                            ModifierInstance.CODEC.listOf()
                                    .optionalFieldOf("modifiers", List.of())
                                    .forGetter(AttachEffect::getModifiers)))
                    .apply(instance, AttachEffect::new));

    private final TriggerPredicate triggerPredicate;
    private final ContinueEffectPredicate continuePredicate;
    private final Holder<EffectMarker> display;
    private final List<ModifierInstance> modifiers;

    public AttachEffect(AbilityTargeting targeting, List<AbilityEffect> effects, Optional<ParticleInfo> particles,
            TriggerPredicate triggerPredicate, ContinueEffectPredicate continuePredicate,
            Optional<Holder<EffectMarker>> display, List<ModifierInstance> modifiers) {
        this(targeting, effects, particles, triggerPredicate, continuePredicate, display.orElse(null), modifiers);
    }

    public AttachEffect(AbilityTargeting targeting, List<AbilityEffect> effects, Optional<ParticleInfo> particles,
            TriggerPredicate triggerPredicate, ContinueEffectPredicate continuePredicate, Holder<EffectMarker> display,
            List<ModifierInstance> modifiers) {
        super(targeting, effects, particles);
        this.triggerPredicate = triggerPredicate;
        this.continuePredicate = continuePredicate;
        this.display = display;
        this.modifiers = ImmutableList.copyOf(modifiers);
    }

    @Override
    public void apply(Entity user, List<BlockPos> blocks, AbilityContext context) {
        List<Entity> targets = getTargeting().getTargets(user, blocks, context);

        applyParticlesToUser(user);

        for (Entity target : targets) {
            applyParticlesToTarget(target);
            target.getData(WotrAttachments.ATTACHED_EFFECTS).attach(this, context);
        }
    }

    @Override
    public MapCodec<? extends AbilityEffect> getCodec() {
        return CODEC;
    }

    public TriggerPredicate getTriggerPredicate() {
        return triggerPredicate;
    }

    public ContinueEffectPredicate getContinuePredicate() {
        return continuePredicate;
    }

    public Optional<Holder<EffectMarker>> getDisplay() {
        return Optional.ofNullable(display);
    }

    public List<ModifierInstance> getModifiers() {
        return modifiers;
    }

}
