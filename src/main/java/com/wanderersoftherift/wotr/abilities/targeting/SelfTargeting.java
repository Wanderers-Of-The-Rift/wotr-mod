package com.wanderersoftherift.wotr.abilities.targeting;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import com.wanderersoftherift.wotr.abilities.effects.predicate.TargetPredicate;
import net.minecraft.world.entity.Entity;

import java.util.List;

public class SelfTargeting extends AbstractTargeting {

    public static final MapCodec<SelfTargeting> CODEC = RecordCodecBuilder
            .mapCodec(instance -> commonFields(instance).apply(instance, SelfTargeting::new));

    public SelfTargeting(TargetPredicate targetPredicate) {
        super(targetPredicate);
    }

    @Override
    public MapCodec<? extends AbstractTargeting> getCodec() {
        return CODEC;
    }

    @Override
    public List<Entity> getTargetsFromEntity(Entity entity, AbilityContext context) {
        WanderersOfTheRift.LOGGER.debug("Targeting Self");

        if (getTargetPredicate().matches(entity, context.caster())) {
            return List.of(entity);
        } else {
            return List.of();
        }
    }
}
