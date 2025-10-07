package com.wanderersoftherift.wotr.abilities.effects.predicate;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Predicate for filtering entities for ability targeting.
 * <p>
 * The predicate can either be ALL (accept everything) NONE (reject everything) or {@link Filter}. The filter predicate
 * wraps the standard {@link EntityPredicate} while providing targeting specific options.
 * </p>
 */
public sealed interface TargetEntityPredicate {

    Codec<TargetEntityPredicate> CODEC = Codec.either(StringRepresentable.fromEnum(Trivial::values), Filter.CODEC)
            .xmap(either -> either.left().isPresent() ? either.left().get() : either.right().get(), predicate -> {
                if (predicate instanceof Filter filter) {
                    return Either.right(filter);
                }
                return Either.left((Trivial) predicate);
            });

    /**
     * @param target  The entity to test
     * @param source  The source target
     * @param context The ability context of the test
     * @return Whether the target is valid (should be affected by the ability)
     */
    boolean matches(Entity target, HitResult source, AbilityContext context);

    /**
     * @param entityPredicate Standard EntityPredicate
     * @param sentiment       Filters the sentiment between the caster and the target entity.
     * @param excludeCaster   Should the caster be excluded from targeting
     * @param excludeSource   Should the source be excluded from targeting
     */
    record Filter(Optional<EntityPredicate> entityPredicate, EntitySentiment sentiment, boolean excludeCaster,
            boolean excludeSource) implements TargetEntityPredicate {

        public static final Codec<Filter> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                EntityPredicate.CODEC.optionalFieldOf("filter").forGetter(Filter::entityPredicate),
                EntitySentiment.CODEC.optionalFieldOf("sentiment", EntitySentiment.ANY).forGetter(Filter::sentiment),
                Codec.BOOL.optionalFieldOf("exclude_caster", false).forGetter(Filter::excludeCaster),
                Codec.BOOL.optionalFieldOf("exclude_source", false).forGetter(Filter::excludeSource)
        ).apply(instance, Filter::new));

        public boolean matches(Entity target, HitResult source, AbilityContext context) {
            if (excludeCaster && target == context.caster()) {
                return false;
            }
            if (excludeSource && source instanceof EntityHitResult entitySource && target == entitySource.getEntity()) {
                return false;
            }
            if (!(context.level() instanceof ServerLevel serverLevel)) {
                return false;
            }
            if (entityPredicate.isPresent()
                    && !entityPredicate.get().matches(serverLevel, context.caster().position(), target)) {
                return false;
            }
            if (!sentiment.matches(target, context.caster())) {
                return false;
            }
            return true;
        }
    }

    enum Trivial implements TargetEntityPredicate, StringRepresentable {
        ALL("all", true),
        NONE("none", false);

        private String id;
        private boolean result;

        Trivial(String id, boolean result) {
            this.id = id;
            this.result = result;
        }

        @Override
        public @NotNull String getSerializedName() {
            return id;
        }

        @Override
        public boolean matches(Entity target, HitResult source, AbilityContext context) {
            return result;
        }
    }

}
