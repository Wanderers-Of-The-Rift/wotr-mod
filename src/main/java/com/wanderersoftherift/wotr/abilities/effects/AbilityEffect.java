package com.wanderersoftherift.wotr.abilities.effects;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import com.wanderersoftherift.wotr.abilities.targeting.AbilityTargeting;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.modifier.effect.ModifierEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attribute;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class AbilityEffect {
    public static final Codec<AbilityEffect> DIRECT_CODEC = WotrRegistries.EFFECTS.byNameCodec()
            .dispatch(AbilityEffect::getCodec, Function.identity());

    private final AbilityTargeting targeting;
    private final List<AbilityEffect> effects;

    public AbilityEffect(AbilityTargeting targeting, List<AbilityEffect> effects) {
        this.targeting = targeting;
        this.effects = effects;
    }

    protected static <T extends AbilityEffect> Products.P2<RecordCodecBuilder.Mu<T>, AbilityTargeting, List<AbilityEffect>> commonFields(
            RecordCodecBuilder.Instance<T> instance) {
        return instance.group(
                AbilityTargeting.DIRECT_CODEC.fieldOf("targeting").forGetter(AbilityEffect::getTargeting),
                Codec.list(AbilityEffect.DIRECT_CODEC)
                        .optionalFieldOf("effects", Collections.emptyList())
                        .forGetter(AbilityEffect::getEffects)
        );
    }

    public abstract MapCodec<? extends AbilityEffect> getCodec();

    public void apply(Entity user, List<BlockPos> blocks, AbilityContext context) {
        for (AbilityEffect effect : getEffects()) {
            effect.apply(user, blocks, context);
        }
    }

    public AbilityTargeting getTargeting() {
        return targeting;
    }

    public List<AbilityEffect> getEffects() {
        return this.effects;
    }

    public Set<Holder<Attribute>> getApplicableAttributes() {
        return getEffects().stream()
                .map(AbilityEffect::getApplicableAttributes)
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }

    /**
     * @param modifierEffect
     * @return Whether the modifier applies to this effect or its children
     */
    public final boolean isRelevant(ModifierEffect modifierEffect) {
        if (isRelevantToThis(modifierEffect)) {
            return true;
        }
        if (getTargeting().isRelevant(modifierEffect)) {
            return true;
        }
        for (AbilityEffect child : effects) {
            if (child.isRelevant(modifierEffect)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines whether a given modifier effect is relevant to this effect. Used to determine what upgrades can apply
     * to abilities that include this effect.
     * 
     * @param modifierEffect
     * @return Whether the modifier is relevant to this effect
     */
    protected boolean isRelevantToThis(ModifierEffect modifierEffect) {
        return false;
    };
}
