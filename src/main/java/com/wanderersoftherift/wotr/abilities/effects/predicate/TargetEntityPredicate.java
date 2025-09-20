package com.wanderersoftherift.wotr.abilities.effects.predicate;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.Locale;
import java.util.Optional;

/**
 * Predicate for filtering entities for ability targeting.
 * <p>
 * The predicate can either be ALL (accept everything) NONE (reject everything) or {@link Filter}. The filter predicate
 * wraps the standard {@link EntityPredicate} while providing targeting specific options.
 * </p>
 */
public interface TargetEntityPredicate {

    TargetEntityPredicate ALL = (target, source, context) -> true;
    TargetEntityPredicate NONE = (target, source, context) -> false;

    BiMap<String, TargetEntityPredicate> BUILT_INS = ImmutableBiMap.of("all", ALL, "none", NONE);

    Codec<TargetEntityPredicate> CODEC = Codec
            .either(Codec.STRING.xmap(val -> BUILT_INS.get(val.toLowerCase(Locale.ROOT)),
                    val -> BUILT_INS.inverse().get(val)), Filter.CODEC)
            .xmap(either -> either.left().orElseGet(() -> either.right().get()), predicate -> {
                if (predicate instanceof Filter filter) {
                    return Either.right(filter);
                }
                return Either.left(predicate);
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

}
