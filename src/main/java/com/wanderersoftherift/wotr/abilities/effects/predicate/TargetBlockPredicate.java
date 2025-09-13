package com.wanderersoftherift.wotr.abilities.effects.predicate;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.Locale;

public interface TargetBlockPredicate {
    TargetBlockPredicate ALL = (target, level) -> true;
    TargetBlockPredicate NONE = (target, level) -> false;

    Codec<TargetBlockPredicate> CODEC = Codec
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
            }), TargetBlockPredicate.Filtered.CODEC);

    boolean matches(BlockPos target, Level level);

    class Filtered implements TargetBlockPredicate {

        public static final Codec<Filtered> CODEC = Codec.unit(new Filtered());

        @Override
        public boolean matches(BlockPos target, Level level) {
            return true;
        }
    }
}
