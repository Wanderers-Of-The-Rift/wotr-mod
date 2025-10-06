package com.wanderersoftherift.wotr.abilities.targeting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.modifier.effect.ModifierEffect;

import java.util.List;
import java.util.function.Function;

/**
 * Interface for classes that provide instant targeting functionality for abilities.
 * <p>
 * Implementations should take each target of origin and produce a new list of targets from it
 * </p>
 */
public interface AbilityTargeting {

    Codec<AbilityTargeting> DIRECT_CODEC = WotrRegistries.EFFECT_TARGETING_TYPES.byNameCodec()
            .dispatch(AbilityTargeting::getCodec, Function.identity());

    /**
     * @return Codec for serializing the AbilityTargeting type
     */
    MapCodec<? extends AbilityTargeting> getCodec();

    /**
     * @param context The context of the ability
     * @param origin  A starting targeting state
     * @return A list of new targeting states. It is expected that each will have effects applied to them
     */
    List<TargetInfo> getTargets(AbilityContext context, TargetInfo origin);

    /**
     * @param modifierEffect
     * @return Whether the given modifier effect applies to the targeting
     */
    default boolean isRelevant(ModifierEffect modifierEffect) {
        return false;
    }
}
