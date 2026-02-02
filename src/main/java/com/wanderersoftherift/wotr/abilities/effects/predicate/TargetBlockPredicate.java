package com.wanderersoftherift.wotr.abilities.effects.predicate;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

/**
 * Predicate for filtering blocks for ability targeting.
 * <p>
 * The predicate can either be ALL (accept everything) NONE (reject everything) or {@link Filter}. The filter predicate
 * wraps {@link BlockPredicate} (there's a couple of these, this is the one that has level context) while providing
 * targeting specific options.
 * </p>
 */
public sealed interface TargetBlockPredicate {
    Codec<TargetBlockPredicate> CODEC = Codec.either(StringRepresentable.fromEnum(Trivial::values), Filter.CODEC)
            .xmap(either -> either.left().isPresent() ? either.left().get() : either.right().get(),
                    predicate -> switch (predicate) {
                        case Filter filter -> Either.right(filter);
                        case Trivial trivial -> Either.left(trivial);
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

    enum Trivial implements TargetBlockPredicate, StringRepresentable {
        ALL("all", true),
        NONE("none",
                false)/*
                       * , ATTACKABLE("attackable", true){
                       * 
                       * @Override public boolean matches(BlockPos target, HitResult source, AbilityContext context) {
                       * return AttackableBlock.isAttackableStatic(context.level().getBlockState(target),
                       * context.level(), target); } }
                       */;

        private String id;
        private boolean result;

        Trivial(String id, boolean result) {
            this.id = id;
            this.result = result;
        }

        @Override
        public boolean matches(BlockPos target, HitResult source, AbilityContext context) {
            return result;
        }

        @Override
        public @NotNull String getSerializedName() {
            return id;
        }
    }
}
