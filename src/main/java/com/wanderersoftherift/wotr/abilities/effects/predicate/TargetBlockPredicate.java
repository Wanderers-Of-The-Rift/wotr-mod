package com.wanderersoftherift.wotr.abilities.effects.predicate;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.Locale;

/**
 * Predicate for filtering blocks for ability targeting.
 * <p>
 * The predicate can either be ALL (accept everything) NONE (reject everything) or {@link Filter}. The filter predicate
 * wraps {@link BlockPredicate} (there's a couple of these, this is the one that has level context) while providing
 * targeting specific options.
 * </p>
 */
public interface TargetBlockPredicate {
    TargetBlockPredicate ALL = (target, source, context) -> true;
    TargetBlockPredicate NONE = (target, source, context) -> false;

    BiMap<String, TargetBlockPredicate> BUILT_INS = ImmutableBiMap.of("all", ALL, "none", NONE);

    Codec<TargetBlockPredicate> CODEC = Codec
            .either(Codec.STRING.xmap(val -> BUILT_INS.get(val.toLowerCase(Locale.ROOT)),
                    val -> BUILT_INS.inverse().get(val)), Filter.CODEC)
            .xmap(either -> either.left().orElseGet(() -> either.right().get()), predicate -> {
                if (predicate instanceof Filter filter) {
                    return Either.right(filter);
                }
                return Either.left(predicate);
            });

    /**
     * @param target  The target block (position with context.level)
     * @param source  The source target
     * @param context The ability context of the test
     * @return Whether the target block is valid (should be affected by the ability)
     */
    boolean matches(BlockPos target, HitResult source, AbilityContext context);

    record Filter(BlockPredicate blockPredicate, boolean matchSource) implements TargetBlockPredicate {

        public static final Codec<Filter> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BlockPredicate.CODEC.optionalFieldOf("filter", BlockPredicate.alwaysTrue())
                        .forGetter(Filter::blockPredicate),
                Codec.BOOL.optionalFieldOf("match_source", false).forGetter(Filter::matchSource)
        ).apply(instance, Filter::new));

        @Override
        public boolean matches(BlockPos target, HitResult source, AbilityContext context) {
            if (!(context.level() instanceof ServerLevel level)) {
                return false;
            }
            if (matchSource) {
                if (source instanceof BlockHitResult blockHit) {
                    Block sourceBlock = context.level().getBlockState(blockHit.getBlockPos()).getBlock();
                    if (!context.level().getBlockState(target).is(sourceBlock)) {
                        return false;
                    }
                } else {
                    return false;
                }
            }
            return blockPredicate.test(level, target);
        }
    }
}
