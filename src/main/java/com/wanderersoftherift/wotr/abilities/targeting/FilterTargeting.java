package com.wanderersoftherift.wotr.abilities.targeting;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import com.wanderersoftherift.wotr.abilities.effects.predicate.TargetBlockPredicate;
import com.wanderersoftherift.wotr.abilities.effects.predicate.TargetEntityPredicate;
import net.minecraft.world.phys.HitResult;

import java.util.ArrayList;
import java.util.List;

/**
 * This targeting just provides filtering on an existing set of targets
 * 
 * @param entities A predicate for what entities can be targeted
 * @param blocks   A predicate for what blocks can be targeted
 */
public record FilterTargeting(TargetEntityPredicate entities, TargetBlockPredicate blocks) implements AbilityTargeting {

    public static final MapCodec<FilterTargeting> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    TargetEntityPredicate.CODEC.optionalFieldOf("entities", TargetEntityPredicate.Trivial.ALL)
                            .forGetter(FilterTargeting::entities),
                    TargetBlockPredicate.CODEC.optionalFieldOf("blocks", TargetBlockPredicate.Trivial.NONE)
                            .forGetter(FilterTargeting::blocks)
            ).apply(instance, FilterTargeting::new));

    @Override
    public MapCodec<? extends AbilityTargeting> getCodec() {
        return CODEC;
    }

    @Override
    public List<TargetInfo> getTargets(AbilityContext context, TargetInfo origin) {
        List<HitResult> results = new ArrayList<>();
        results.addAll(
                origin.targetEntityHitResults()
                        .filter(x -> entities().matches(x.getEntity(), origin.source(), context))
                        .toList());
        results.addAll(
                origin.targetBlockHitResults()
                        .filter(x -> blocks.matches(x.getBlockPos(), origin.source(), context))
                        .toList());
        if (results.isEmpty()) {
            return List.of();
        }
        return List.of(new TargetInfo(origin.source(), results));
    }

}
