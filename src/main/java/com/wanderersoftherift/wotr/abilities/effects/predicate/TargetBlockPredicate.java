package com.wanderersoftherift.wotr.abilities.effects.predicate;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.phys.HitResult;

import java.util.Locale;

public interface TargetBlockPredicate {
    TargetBlockPredicate ALL = (target, source, context) -> true;
    TargetBlockPredicate NONE = (target, source, context) -> false;

    Codec<TargetBlockPredicate> CODEC = Codec.either(Codec.STRING.xmap(val -> switch (val.toLowerCase(Locale.ROOT)) {
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
    }), TargetBlockPredicate.Filtered.CODEC)
            .xmap(either -> either.left().orElseGet(() -> either.right().get()), predicate -> {
                if (predicate instanceof Filtered filtered) {
                    return Either.right(filtered);
                }
                return Either.left(predicate);
            });

    boolean matches(BlockPos target, HitResult source, AbilityContext context);

    record Filtered(BlockPredicate blockPredicate) implements TargetBlockPredicate {

        public static final Codec<Filtered> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BlockPredicate.CODEC.optionalFieldOf("filter", BlockPredicate.alwaysTrue())
                        .forGetter(Filtered::blockPredicate)
        ).apply(instance, Filtered::new));

        @Override
        public boolean matches(BlockPos target, HitResult source, AbilityContext context) {
            if (!(context.level() instanceof ServerLevel level)) {
                return false;
            }
            return blockPredicate.test(level, target);
        }
    }
}
