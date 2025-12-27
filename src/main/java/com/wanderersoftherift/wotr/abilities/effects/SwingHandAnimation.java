package com.wanderersoftherift.wotr.abilities.effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import com.wanderersoftherift.wotr.abilities.targeting.TargetInfo;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;

public record SwingHandAnimation(InteractionHand hand) implements AbilityEffect {
    public static final MapCodec<SwingHandAnimation> CODEC = Codec.STRING
            .xmap(it -> InteractionHand.valueOf(it.toUpperCase()), it -> it.toString().toLowerCase())
            .optionalFieldOf("interaction_hand", InteractionHand.MAIN_HAND)
            .xmap(SwingHandAnimation::new, SwingHandAnimation::hand);

    @Override
    public MapCodec<? extends AbilityEffect> getCodec() {
        return CODEC;
    }

    @Override
    public void apply(AbilityContext context, TargetInfo targetInfo) {
        targetInfo.targetEntities()
                .filter(it -> it instanceof LivingEntity)
                .forEach(it -> ((LivingEntity) it).swing(hand, true));
    }
}
