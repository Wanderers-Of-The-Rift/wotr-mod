package com.wanderersoftherift.wotr.abilities.effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import com.wanderersoftherift.wotr.abilities.targeting.TargetInfo;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.modifier.effect.ModifierEffect;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface AbilityEffect {
    Codec<AbilityEffect> DIRECT_CODEC = WotrRegistries.EFFECTS.byNameCodec()
            .dispatch(AbilityEffect::getCodec, Function.identity());

    MapCodec<? extends AbilityEffect> getCodec();

    void apply(AbilityContext context, TargetInfo targetInfo);

    /**
     * @return A set of attributes that are applicable to this effect
     */
    default Set<Holder<Attribute>> getApplicableAttributes() {
        return Set.of();
    }

    /**
     * @param modifierEffect
     * @return Whether the modifier applies to this effect or its children
     */
    default boolean isRelevant(ModifierEffect modifierEffect) {
        return false;
    }

    static Set<Holder<Attribute>> getApplicableAttributes(List<AbilityEffect> effects) {
        return effects.stream()
                .map(AbilityEffect::getApplicableAttributes)
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }

}
