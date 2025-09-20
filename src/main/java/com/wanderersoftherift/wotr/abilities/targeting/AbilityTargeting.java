package com.wanderersoftherift.wotr.abilities.targeting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.modifier.effect.ModifierEffect;

import java.util.List;
import java.util.function.Function;

public interface AbilityTargeting {

    Codec<AbilityTargeting> DIRECT_CODEC = WotrRegistries.EFFECT_TARGETING_TYPES.byNameCodec()
            .dispatch(AbilityTargeting::getCodec, Function.identity());

    MapCodec<? extends AbilityTargeting> getCodec();

    List<TargetInfo> getTargets(AbilityContext context, TargetInfo origin);

    default boolean isRelevant(ModifierEffect modifierEffect) {
        return false;
    }
}
