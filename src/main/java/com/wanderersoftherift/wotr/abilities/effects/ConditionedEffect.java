package com.wanderersoftherift.wotr.abilities.effects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import com.wanderersoftherift.wotr.abilities.effects.util.ParticleInfo;
import com.wanderersoftherift.wotr.abilities.targeting.AbilityTargeting;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ConditionedEffect extends AbilityEffect {
    public static final MapCodec<ConditionedEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> AbilityEffect
            .commonFields(instance)
            .and(
                    instance.group(
                            AbilityEffect.DIRECT_CODEC.listOf()
                                    .optionalFieldOf("present", Collections.emptyList())
                                    .forGetter(ConditionedEffect::effectsTrue),
                            AbilityEffect.DIRECT_CODEC.listOf()
                                    .optionalFieldOf("missing", Collections.emptyList())
                                    .forGetter(ConditionedEffect::effectsFalse),
                            ResourceLocation.CODEC.fieldOf("condition_name").forGetter(ConditionedEffect::condition)
                    )
            )
            .apply(instance, ConditionedEffect::new));

    private final List<AbilityEffect> effectsTrue;

    private final List<AbilityEffect> effectsFalse;
    private final ResourceLocation condition;

    public ConditionedEffect(AbilityTargeting targeting, List<AbilityEffect> effects, Optional<ParticleInfo> particles,
            List<AbilityEffect> effectsTrue, List<AbilityEffect> effectsFalse, ResourceLocation condition) {
        super(targeting, effects, particles);

        this.effectsTrue = effectsTrue;
        this.effectsFalse = effectsFalse;
        this.condition = condition;
    }

    private List<AbilityEffect> effectsTrue() {
        return effectsTrue;
    }

    private List<AbilityEffect> effectsFalse() {
        return effectsFalse;
    }

    private ResourceLocation condition() {
        return condition;
    }

    @Override
    public MapCodec<? extends AbilityEffect> getCodec() {
        return CODEC;
    }

    @Override
    public void apply(Entity user, List<BlockPos> blocks, AbilityContext context) {
        super.apply(user, blocks, context);

        var condition = context.conditions().contains(condition());

        for (AbilityEffect effect : (condition ? effectsTrue() : effectsFalse())) {
            effect.apply(user, blocks, context);
        }
    }
}
