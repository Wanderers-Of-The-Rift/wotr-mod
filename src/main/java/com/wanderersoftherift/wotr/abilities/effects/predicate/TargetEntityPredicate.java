package com.wanderersoftherift.wotr.abilities.effects.predicate;

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

public interface TargetEntityPredicate {

    TargetEntityPredicate ALL = (target, source, context) -> true;
    TargetEntityPredicate NONE = (target, source, context) -> false;

    Codec<TargetEntityPredicate> CODEC = Codec.either(Codec.STRING.xmap(val -> switch (val.toLowerCase(Locale.ROOT)) {
        case "all" -> ALL;
        case "none" -> NONE;
        default -> throw new RuntimeException("Unexpected value for predicate: '" + val + "'");
    }, predicate -> {
        if (predicate == ALL) {
            return "all";
        } else if (predicate == NONE) {
            return "none";
        }
        throw new RuntimeException("Not a constant predicate");
    }), Filtered.CODEC).xmap(either -> either.left().orElseGet(() -> either.right().get()), predicate -> {
        if (predicate instanceof Filtered filtered) {
            return Either.right(filtered);
        }
        return Either.left(predicate);
    });

    boolean matches(Entity target, HitResult source, AbilityContext context);

    record Filtered(Optional<EntityPredicate> entityPredicate, EntitySentiment sentiment, boolean excludeCaster,
            boolean excludeSource) implements TargetEntityPredicate {

        public static final Codec<Filtered> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                EntityPredicate.CODEC.optionalFieldOf("filter").forGetter(Filtered::entityPredicate),
                EntitySentiment.CODEC.optionalFieldOf("sentiment", EntitySentiment.ANY).forGetter(Filtered::sentiment),
                Codec.BOOL.optionalFieldOf("exclude_caster", false).forGetter(Filtered::excludeCaster),
                Codec.BOOL.optionalFieldOf("exclude_source", false).forGetter(Filtered::excludeSource)
        ).apply(instance, Filtered::new));

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
