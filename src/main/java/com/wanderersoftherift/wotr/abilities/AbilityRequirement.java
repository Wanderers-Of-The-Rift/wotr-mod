package com.wanderersoftherift.wotr.abilities;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.modifier.effect.ModifierEffect;

import java.util.function.Function;

/**
 * An ability requirement defines a need for an ability to be activated or continue being activated, and an optional
 * cost for activation.
 */
public interface AbilityRequirement {

    Codec<AbilityRequirement> CODEC = WotrRegistries.ABILITY_REQUIREMENT_TYPES.byNameCodec()
            .dispatch(AbilityRequirement::getCodec, Function.identity());

    MapCodec<? extends AbilityRequirement> getCodec();

    boolean check(AbilityContext context);

    default void pay(AbilityContext context) {
    }

    default boolean checkAndPay(AbilityContext context) {
        if (check(context)) {
            pay(context);
            return true;
        }
        return false;
    }

    default boolean isRelevant(ModifierEffect modifierEffect) {
        return false;
    }
}
