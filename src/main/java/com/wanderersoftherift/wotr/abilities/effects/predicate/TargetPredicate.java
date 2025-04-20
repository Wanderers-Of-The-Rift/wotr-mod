package com.wanderersoftherift.wotr.abilities.effects.predicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

import java.util.Optional;

public record TargetPredicate(Optional<EntityPredicate> entityPredicate, EntitySentiment sentiment,
        boolean excludeCaster) {

    public static final Codec<TargetPredicate> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(EntityPredicate.CODEC.optionalFieldOf("entity").forGetter(TargetPredicate::entityPredicate),
                    EntitySentiment.CODEC.optionalFieldOf("sentiment", EntitySentiment.ANY)
                            .forGetter(TargetPredicate::sentiment),
                    Codec.BOOL.optionalFieldOf("exclude_caster", false).forGetter(TargetPredicate::excludeCaster))
            .apply(instance, TargetPredicate::new));

    public TargetPredicate() {
        this(Optional.empty(), EntitySentiment.ANY, false);
    }

    public boolean matches(Entity target, Entity caster) {
        if (excludeCaster && target == caster) {
            return false;
        }
        if (!(caster.level() instanceof ServerLevel serverLevel)) {
            return false;
        }
        if (entityPredicate.isPresent() && !entityPredicate.get().matches(serverLevel, caster.position(), target)) {
            return false;
        }
        if (!sentiment.matches(target, caster)) {
            return false;
        }
        return true;
    }

    public static class Builder {
        private Optional<EntityPredicate> entityPredicate = Optional.empty();
        private EntitySentiment sentiment = EntitySentiment.ANY;
        private boolean excludeCaster = false;

        /**
         * @param predicate an entity predicate to filters possible targets
         * @return
         */
        public Builder withEntityPredicate(EntityPredicate predicate) {
            entityPredicate = Optional.of(predicate);
            return this;
        }

        /**
         * @param sentiment a sentiment to filter possible targets
         * @return
         */
        public Builder withSentiment(EntitySentiment sentiment) {
            this.sentiment = sentiment;
            return this;
        }

        /**
         * Excludes the caster from being a valid target
         * 
         * @return
         */
        public Builder excludeCaster() {
            this.excludeCaster = true;
            return this;
        }

        public TargetPredicate build() {
            return new TargetPredicate(entityPredicate, sentiment, excludeCaster);
        }
    }

}
