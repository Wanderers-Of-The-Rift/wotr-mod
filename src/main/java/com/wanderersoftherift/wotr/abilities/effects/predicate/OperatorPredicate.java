package com.wanderersoftherift.wotr.abilities.effects.predicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.HitResult;

import java.util.List;

/**
 * Combines multiple {@link TargetEntityPredicate}s using logical operators. Supports three logical operations: AND, OR
 * and NOT.
 * 
 * @param operator   The logical operator to apply to the conditions
 * @param conditions List of predicates to evaluate. Can also contain other OperatorPredicates for nested logic.
 */
public record OperatorPredicate(TargetEntityPredicate.LogicalOperator operator, List<TargetEntityPredicate> conditions)
        implements TargetEntityPredicate {
    public static final Codec<OperatorPredicate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            TargetEntityPredicate.LogicalOperator.CODEC.optionalFieldOf("operator", LogicalOperator.OR)
                    .forGetter(OperatorPredicate::operator),
            Codec.lazyInitialized(() -> TargetEntityPredicate.NON_TRIVIAL_CODEC)
                    .listOf()
                    .fieldOf("conditions")
                    .forGetter(OperatorPredicate::conditions)
    ).apply(instance, OperatorPredicate::new));

    @Override
    public boolean matches(Entity target, HitResult source, AbilityContext context) {
        return switch (operator) {
            case AND -> conditions.stream().allMatch(predicate -> predicate.matches(target, source, context));
            case OR -> conditions.stream().anyMatch(predicate -> predicate.matches(target, source, context));
            case NOT -> conditions.stream().noneMatch(predicate -> predicate.matches(target, source, context));
        };
    }
}
