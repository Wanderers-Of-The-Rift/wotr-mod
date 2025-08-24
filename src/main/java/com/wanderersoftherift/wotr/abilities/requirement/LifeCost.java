package com.wanderersoftherift.wotr.abilities.requirement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import com.wanderersoftherift.wotr.abilities.AbilityRequirement;

public record LifeCost(float amount, boolean consume) implements AbilityRequirement {
    public static final MapCodec<LifeCost> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.FLOAT.fieldOf("amount").forGetter(LifeCost::amount),
            Codec.BOOL.optionalFieldOf("consume", true).forGetter(LifeCost::consume)
    ).apply(instance, LifeCost::new));

    @Override
    public MapCodec<? extends AbilityRequirement> getCodec() {
        return CODEC;
    }

    @Override
    public boolean check(AbilityContext context) {
        return context.caster().getHealth() > amount;
    }

    @Override
    public void pay(AbilityContext context) {
        if (consume) {
            context.caster().setHealth(context.caster().getHealth() - amount);
        }
    }
}
