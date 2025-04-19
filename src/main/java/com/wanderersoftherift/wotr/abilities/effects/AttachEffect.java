package com.wanderersoftherift.wotr.abilities.effects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import com.wanderersoftherift.wotr.abilities.effects.marker.EffectMarker;
import com.wanderersoftherift.wotr.abilities.effects.predicate.ContinueEffectPredicate;
import com.wanderersoftherift.wotr.abilities.effects.predicate.TriggerPredicate;
import com.wanderersoftherift.wotr.abilities.effects.util.ParticleInfo;
import com.wanderersoftherift.wotr.abilities.targeting.AbstractTargeting;
import com.wanderersoftherift.wotr.init.ModAttachments;
import com.wanderersoftherift.wotr.init.RegistryEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;
import java.util.Optional;

/**
 * AttachEffect attaches all of its child effects to each target entity, with a durationTicks
 */
public class AttachEffect extends AbstractEffect {

    public static final MapCodec<AttachEffect> CODEC = RecordCodecBuilder
            .mapCodec(instance -> AbstractEffect.commonFields(instance)
                    .and(TriggerPredicate.CODEC.optionalFieldOf("trigger", new TriggerPredicate())
                            .forGetter(AttachEffect::getTriggerPredicate))
                    .and(ContinueEffectPredicate.CODEC.optionalFieldOf("continue", new ContinueEffectPredicate())
                            .forGetter(AttachEffect::getContinuePredicate))
                    .and(RegistryFixedCodec.create(RegistryEvents.EFFECT_MARKER_REGISTRY)
                            .optionalFieldOf("display")
                            .forGetter(AttachEffect::getDisplay))
                    .apply(instance, AttachEffect::new));

    private final TriggerPredicate triggerPredicate;
    private final ContinueEffectPredicate continuePredicate;
    private final Holder<EffectMarker> display;

    public AttachEffect(AbstractTargeting targeting, List<AbstractEffect> effects, Optional<ParticleInfo> particles,
            TriggerPredicate triggerPredicate, ContinueEffectPredicate continuePredicate,
            Optional<Holder<EffectMarker>> display) {
        this(targeting, effects, particles, triggerPredicate, continuePredicate, display.orElse(null));
    }

    public AttachEffect(AbstractTargeting targeting, List<AbstractEffect> effects, Optional<ParticleInfo> particles,
            TriggerPredicate triggerPredicate, ContinueEffectPredicate continuePredicate,
            Holder<EffectMarker> display) {
        super(targeting, effects, particles);
        this.triggerPredicate = triggerPredicate;
        this.continuePredicate = continuePredicate;
        this.display = display;
    }

    @Override
    public void apply(Entity user, List<BlockPos> blocks, AbilityContext context) {
        List<Entity> targets = getTargeting().getTargets(user, blocks, context);

        applyParticlesToUser(user);

        for (Entity target : targets) {
            applyParticlesToTarget(target);

            if (target instanceof LivingEntity livingTarget) {
                target.getData(ModAttachments.ATTACHED_EFFECTS).attach(livingTarget, this, context);
            }
        }
    }

    @Override
    public MapCodec<? extends AbstractEffect> getCodec() {
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

}
