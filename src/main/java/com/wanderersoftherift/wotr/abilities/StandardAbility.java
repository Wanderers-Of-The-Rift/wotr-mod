package com.wanderersoftherift.wotr.abilities;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.attachment.ManaData;
import com.wanderersoftherift.wotr.abilities.effects.AbilityEffect;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.init.WotrAttributes;
import com.wanderersoftherift.wotr.modifier.effect.AbstractModifierEffect;
import com.wanderersoftherift.wotr.modifier.effect.AttributeModifierEffect;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class StandardAbility extends Ability {

    public static final MapCodec<StandardAbility> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    ResourceLocation.CODEC.fieldOf("icon").forGetter(StandardAbility::getIcon),
                    ResourceLocation.CODEC.optionalFieldOf("small_icon").forGetter(StandardAbility::getSmallIcon),
                    Codec.INT.fieldOf("cooldown").forGetter(Ability::getBaseCooldown),
                    Codec.INT.optionalFieldOf("mana_cost", 0).forGetter(StandardAbility::getBaseManaCost),
                    Codec.list(AbilityEffect.DIRECT_CODEC)
                            .optionalFieldOf("effects", Collections.emptyList())
                            .forGetter(StandardAbility::getEffects)
            ).apply(instance, StandardAbility::new));

    private final List<AbilityEffect> effects;

    public StandardAbility(ResourceLocation icon, Optional<ResourceLocation> smallIcon, int baseCooldown, int manaCost,
            List<AbilityEffect> effects) {
        super(icon, smallIcon, baseCooldown, manaCost);
        this.effects = new ArrayList<>(effects);
    }

    @Override
    public MapCodec<? extends Ability> getCodec() {
        return CODEC;
    }

    @Override
    public boolean canActivate(AbilityContext context) {
        if (context.slot() != null
                && context.caster().getData(WotrAttachments.ABILITY_COOLDOWNS).isOnCooldown(context.slot())) {
            return false;
        }
        float manaCost = context.getAbilityAttribute(WotrAttributes.MANA_COST, getBaseManaCost());
        ManaData manaData = context.caster().getData(WotrAttachments.MANA);
        if (manaCost > 0 && manaData.getAmount() < manaCost) {
            return false;
        }
        return true;
    }

    @Override
    public boolean activate(AbilityContext context) {
        LivingEntity caster = context.caster();
        float manaCost = context.getAbilityAttribute(WotrAttributes.MANA_COST, getBaseManaCost());
        ManaData manaData = context.caster().getData(WotrAttachments.MANA);
        manaData.useAmount(manaCost);
        this.getEffects().forEach(effect -> effect.apply(caster, List.of(), context));

        context.applyCooldown();
        return true;
    }

    private List<AbilityEffect> getEffects() {
        return effects;
    }

    public boolean isRelevantModifier(AbstractModifierEffect modifierEffect) {
        if (modifierEffect instanceof AttributeModifierEffect attributeModifierEffect) {
            Holder<Attribute> attribute = attributeModifierEffect.getAttribute();
            if (WotrAttributes.COOLDOWN.equals(attribute) && getBaseCooldown() > 0) {
                return true;
            }
            if (WotrAttributes.MANA_COST.equals(attribute) && getBaseManaCost() > 0) {
                return true;
            }
        }
        for (AbilityEffect effect : effects) {
            if (effect.isRelevant(modifierEffect)) {
                return true;
            }
        }
        return false;
    }
}
