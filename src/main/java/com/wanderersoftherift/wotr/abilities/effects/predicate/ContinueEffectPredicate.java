package com.wanderersoftherift.wotr.abilities.effects.predicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

import java.util.Optional;

/**
 * Predicate for determining if an attached effect should continue
 * 
 * @param targetPredicate Sub-predicate for whether the target is still valid
 * @param casterPredicate Sub-predicate for whether the caster is still valid
 * @param duration        Maximum duration the effect should continue (or 0 for forever)
 * @param maxTriggerCount Maximum number of times the effect should trigger (or 0 for no limit)
 */
public record ContinueEffectPredicate(Optional<EntityPredicate> targetPredicate,
        Optional<EntityPredicate> casterPredicate, int duration, int maxTriggerCount) {

    public static final Codec<ContinueEffectPredicate> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(EntityPredicate.CODEC.optionalFieldOf("target").forGetter(ContinueEffectPredicate::targetPredicate),
                    EntityPredicate.CODEC.optionalFieldOf("caster").forGetter(ContinueEffectPredicate::casterPredicate),
                    Codec.INT.optionalFieldOf("duration", 0).forGetter(ContinueEffectPredicate::duration),
                    Codec.INT.optionalFieldOf("max_trigger_count", 0)
                            .forGetter(ContinueEffectPredicate::maxTriggerCount))
            .apply(instance, ContinueEffectPredicate::new));

    public ContinueEffectPredicate() {
        this(Optional.empty(), Optional.empty(), 0, 1);
    }

    public boolean matches(Entity target, int tick, int triggerCount, Entity caster) {
        if (duration != 0 && tick >= duration) {
            return false;
        }
        if (maxTriggerCount != 0 && triggerCount >= maxTriggerCount) {
            return false;
        }
        if (!(caster.level() instanceof ServerLevel serverLevel)) {
            return false;
        }
        if (targetPredicate.isPresent() && !targetPredicate.get().matches(serverLevel, caster.position(), target)) {
            return false;
        }
        if (casterPredicate.isPresent() && !casterPredicate.get().matches(serverLevel, caster.position(), caster)) {
            return false;
        }
        return true;
    }
}
