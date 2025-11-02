package com.wanderersoftherift.wotr.abilities.effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import com.wanderersoftherift.wotr.abilities.targeting.TargetInfo;
import com.wanderersoftherift.wotr.init.WotrAttributes;
import com.wanderersoftherift.wotr.modifier.effect.AttributeModifierEffect;
import com.wanderersoftherift.wotr.modifier.effect.ModifierEffect;
import net.minecraft.world.entity.LivingEntity;

/**
 * Effect that applies healing to target entities
 */
public record HealEffect(float amount) implements AbilityEffect {
    public static final MapCodec<HealEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.FLOAT.fieldOf("amount").forGetter(HealEffect::amount)
    ).apply(instance, HealEffect::new));

    @Override
    public MapCodec<? extends AbilityEffect> getCodec() {
        return CODEC;
    }

    @Override
    public void apply(AbilityContext context, TargetInfo targetInfo) {
        float finalHealAmount = context.getAbilityAttribute(WotrAttributes.HEAL_POWER, amount);
        targetInfo.targetEntities()
                .filter(LivingEntity.class::isInstance)
                .map(LivingEntity.class::cast)
                .forEach(target -> target.heal(finalHealAmount));
    }

    @Override
    public boolean isRelevant(ModifierEffect modifierEffect) {
        return modifierEffect instanceof AttributeModifierEffect attributeModifierEffect
                && WotrAttributes.HEAL_POWER.equals(attributeModifierEffect.attribute());
    }
}
