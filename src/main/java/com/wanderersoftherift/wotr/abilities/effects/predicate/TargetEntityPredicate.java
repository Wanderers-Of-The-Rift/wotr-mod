package com.wanderersoftherift.wotr.abilities.effects.predicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

import java.util.Locale;
import java.util.Optional;

public interface TargetEntityPredicate {

    TargetEntityPredicate ALL = (target, caster) -> true;
    TargetEntityPredicate NONE = (target, caster) -> false;

    Codec<TargetEntityPredicate> CODEC = Codec
            .withAlternative(Codec.STRING.xmap(val -> switch (val.toLowerCase(Locale.ROOT)) {
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
            }), Filtered.CODEC);

    boolean matches(Entity target, Entity caster);

    record Filtered(Optional<EntityPredicate> entityPredicate, EntitySentiment sentiment, boolean excludeCaster)
            implements TargetEntityPredicate {

        public static final Codec<Filtered> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                EntityPredicate.CODEC.optionalFieldOf("entity").forGetter(Filtered::entityPredicate),
                EntitySentiment.CODEC.optionalFieldOf("sentiment", EntitySentiment.ANY).forGetter(Filtered::sentiment),
                Codec.BOOL.optionalFieldOf("exclude_caster", false).forGetter(Filtered::excludeCaster)
        ).apply(instance, Filtered::new));

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
    }

}
