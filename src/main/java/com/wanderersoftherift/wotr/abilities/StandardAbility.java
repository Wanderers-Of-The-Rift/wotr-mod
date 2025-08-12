package com.wanderersoftherift.wotr.abilities;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.attachment.ManaData;
import com.wanderersoftherift.wotr.abilities.effects.AbilityEffect;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.init.WotrAttributes;
import com.wanderersoftherift.wotr.modifier.WotrEquipmentSlot;
import com.wanderersoftherift.wotr.modifier.effect.AbstractModifierEffect;
import com.wanderersoftherift.wotr.modifier.effect.AttributeModifierEffect;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class StandardAbility extends Ability {

    public static final MapCodec<StandardAbility> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    ResourceLocation.CODEC.fieldOf("icon").forGetter(StandardAbility::getIcon),
                    ResourceLocation.CODEC.optionalFieldOf("smallIcon").forGetter(StandardAbility::getSmallIcon),
                    Codec.INT.fieldOf("cooldown").forGetter(Ability::getBaseCooldown),
                    Codec.INT.optionalFieldOf("mana_cost", 0).forGetter(StandardAbility::getBaseManaCost),
                    Codec.list(AbilityEffect.DIRECT_CODEC)
                            .optionalFieldOf("effects", Collections.emptyList())
                            .forGetter(StandardAbility::getEffects)
            ).apply(instance, StandardAbility::new));

    private final List<AbilityEffect> effects;

    public StandardAbility(ResourceLocation icon, Optional<ResourceLocation> smallIcon, int baseCooldown, int manaCost,
            List<AbilityEffect> effects) {
        super(icon, smallIcon, baseCooldown);
        this.effects = new ArrayList<>(effects);
        setBaseManaCost(manaCost);
    }

    public List<AbilityEffect> getEffects() {
        return effects;
    }

    @Override
    public MapCodec<? extends Ability> getCodec() {
        return CODEC;
    }

    @Override
    public boolean onActivate(LivingEntity caster, ItemStack abilityItem, @Nullable WotrEquipmentSlot slot) {
        if (!this.canUse(caster)) {
            if (caster instanceof ServerPlayer player) {
                // TODO: Proper translatable component, or maybe we remove this?
                player.displayClientMessage(Component.literal("You cannot use this"), true);
            }
            return false;
        }
        if (slot != null && caster.getData(WotrAttachments.ABILITY_COOLDOWNS).isOnCooldown(slot)) {
            return false;
        }
        AbilityContext abilityContext = new AbilityContext(caster, abilityItem);
        abilityContext.enableUpgradeModifiers();
        try {
            int manaCost = (int) abilityContext.getAbilityAttribute(WotrAttributes.MANA_COST, getBaseManaCost());
            ManaData manaData = caster.getData(WotrAttachments.MANA);
            if (manaCost > 0) {
                if (manaData.getAmount() < manaCost) {
                    return false;
                }
            }
            if (caster instanceof ServerPlayer player) {
                manaData.useAmount(manaCost);
                this.getEffects().forEach(effect -> effect.apply(player, new ArrayList<>(), abilityContext));
            }
            if (slot != null) {
                caster.getData(WotrAttachments.ABILITY_COOLDOWNS)
                        .setCooldown(slot,
                                (int) abilityContext.getAbilityAttribute(WotrAttributes.COOLDOWN, getBaseCooldown()));
            }
        } finally {
            abilityContext.disableUpgradeModifiers();
        }
        return true;
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
