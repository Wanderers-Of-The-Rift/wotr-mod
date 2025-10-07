package com.wanderersoftherift.wotr.abilities.requirement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import com.wanderersoftherift.wotr.abilities.AbilityRequirement;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.init.WotrAttributes;
import com.wanderersoftherift.wotr.modifier.effect.AttributeModifierEffect;
import com.wanderersoftherift.wotr.modifier.effect.ModifierEffect;

public record CooldownCost(int ticks) implements AbilityRequirement {
    public static final MapCodec<CooldownCost> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.optionalFieldOf("ticks", 20).forGetter(CooldownCost::ticks)
    ).apply(instance, CooldownCost::new));

    @Override
    public MapCodec<? extends AbilityRequirement> getCodec() {
        return CODEC;
    }

    @Override
    public boolean check(AbilityContext context) {
        return !context.caster().getData(WotrAttachments.ABILITY_COOLDOWNS).isOnCooldown(context.source());
    }

    @Override
    public void pay(AbilityContext context) {
        context.caster()
                .getData(WotrAttachments.ABILITY_COOLDOWNS)
                .setCooldown(context.source(), (int) context.getAbilityAttribute(WotrAttributes.COOLDOWN, ticks));
    }

    @Override
    public boolean isRelevant(ModifierEffect modifierEffect) {
        return ticks > 0 && modifierEffect instanceof AttributeModifierEffect attributeModifierEffect
                && WotrAttributes.COOLDOWN.equals(attributeModifierEffect.attribute());
    }
}
