package com.wanderersoftherift.wotr.abilities.requirement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import com.wanderersoftherift.wotr.abilities.AbilityRequirement;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.init.WotrAttributes;
import com.wanderersoftherift.wotr.modifier.effect.AbstractModifierEffect;
import com.wanderersoftherift.wotr.modifier.effect.AttributeModifierEffect;

public record ManaCost(float amount) implements AbilityRequirement {
    public static final MapCodec<ManaCost> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.FLOAT.optionalFieldOf("amount", 0f).forGetter(ManaCost::amount)
    ).apply(instance, ManaCost::new));

    @Override
    public MapCodec<? extends AbilityRequirement> getCodec() {
        return CODEC;
    }

    @Override
    public boolean check(AbilityContext context) {
        float manaCost = context.getAbilityAttribute(WotrAttributes.MANA_COST, amount);
        return context.caster().getData(WotrAttachments.MANA).getAmount() >= manaCost;
    }

    @Override
    public void pay(AbilityContext context) {
        float manaCost = context.getAbilityAttribute(WotrAttributes.MANA_COST, amount);
        context.caster().getData(WotrAttachments.MANA).useAmount(manaCost);
    }

    @Override
    public boolean isRelevantModifier(AbstractModifierEffect modifierEffect) {
        return modifierEffect instanceof AttributeModifierEffect attributeModifierEffect
                && WotrAttributes.MANA_COST.equals(attributeModifierEffect.getAttribute());
    }
}
