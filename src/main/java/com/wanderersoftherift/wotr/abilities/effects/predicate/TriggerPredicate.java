package com.wanderersoftherift.wotr.abilities.effects.predicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

import java.util.Optional;

/**
 * Predicate for determining when an effect should trigger
 * 
 * @param targetPredicate Sub-predicate for checking if the target is in a state to trigger the effect
 * @param casterPredicate Sub-predicate for checking if the caster is in a state to trigger the effect
 * @param frequency       How many ticks between moments the effect might trigger
 * @param initialDelay    How many ticks before the effect can first trigger
 */
public record TriggerPredicate(Optional<EntityPredicate> targetPredicate, Optional<EntityPredicate> casterPredicate,
        int frequency, int initialDelay) {

    public static final Codec<TriggerPredicate> CODEC = RecordCodecBuilder
            .create(instance -> instance
                    .group(EntityPredicate.CODEC.optionalFieldOf("target").forGetter(TriggerPredicate::targetPredicate),
                            EntityPredicate.CODEC.optionalFieldOf("caster")
                                    .forGetter(TriggerPredicate::casterPredicate),
                            Codec.INT.optionalFieldOf("frequency", 1).forGetter(TriggerPredicate::frequency),
                            Codec.INT.optionalFieldOf("initial_delay", 0).forGetter(TriggerPredicate::initialDelay))
                    .apply(instance, TriggerPredicate::new));

    public TriggerPredicate() {
        this(Optional.empty(), Optional.empty(), 1, 0);
    }

    public boolean matches(Entity target, int tick, Entity caster) {
        if (tick < initialDelay) {
            return false;
        }
        if ((tick - initialDelay) % frequency != 0) {
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
