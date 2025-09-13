package com.wanderersoftherift.wotr.abilities.requirement;

import com.mojang.math.Constants;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import com.wanderersoftherift.wotr.abilities.AbilityRequirement;
import com.wanderersoftherift.wotr.abilities.AbilityResource;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.init.WotrAttributes;
import com.wanderersoftherift.wotr.modifier.effect.AttributeModifierEffect;
import com.wanderersoftherift.wotr.modifier.effect.ModifierEffect;
import net.minecraft.core.Holder;

public record ManaCost(Holder<AbilityResource> resource, float amount, boolean consume) implements AbilityRequirement {

    public static final MapCodec<ManaCost> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            AbilityResource.HOLDER_CODEC.fieldOf("resource_type").forGetter(ManaCost::resource),
            Codec.FLOAT.optionalFieldOf("amount", Constants.EPSILON).forGetter(ManaCost::amount),
            Codec.BOOL.optionalFieldOf("consume", true).forGetter(ManaCost::consume)
    ).apply(instance, ManaCost::new));

    @Override
    public MapCodec<? extends AbilityRequirement> getCodec() {
        return CODEC;
    }

    @Override
    public boolean check(AbilityContext context) {
        float manaCost = context.getAbilityAttribute(WotrAttributes.MANA_COST, amount);
        return context.caster().getData(WotrAttachments.MANA).getAmount(resource) >= manaCost;
    }

    @Override
    public void pay(AbilityContext context) {
        if (consume) {
            float manaCost = context.getAbilityAttribute(WotrAttributes.MANA_COST, amount);
            context.caster().getData(WotrAttachments.MANA).useAmount(resource, manaCost);
        }
    }

    @Override
    public boolean isRelevant(ModifierEffect modifierEffect) {
        return consume && modifierEffect instanceof AttributeModifierEffect attributeModifierEffect
                && WotrAttributes.MANA_COST.equals(attributeModifierEffect.attribute());
    }
}
