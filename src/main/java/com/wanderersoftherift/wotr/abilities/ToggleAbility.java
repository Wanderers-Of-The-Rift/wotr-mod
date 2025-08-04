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
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ToggleAbility extends Ability {

    public static final MapCodec<ToggleAbility> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    ResourceLocation.CODEC.fieldOf("icon").forGetter(ToggleAbility::getIcon),
                    ResourceLocation.CODEC.optionalFieldOf("small_icon").forGetter(ToggleAbility::getSmallIcon),
                    Codec.INT.optionalFieldOf("cooldown", 0).forGetter(ToggleAbility::getBaseCooldown),
                    Codec.INT.optionalFieldOf("warmup_time", 0).forGetter(ToggleAbility::getWarmupTime),
                    Codec.INT.optionalFieldOf("mana_cost", 0).forGetter(ToggleAbility::getBaseManaCost),
                    Codec.BOOL.optionalFieldOf("mana_required", true).forGetter(ToggleAbility::isManaRequired),
                    Codec.list(AbilityEffect.DIRECT_CODEC)
                            .optionalFieldOf("activation_effects", Collections.emptyList())
                            .forGetter(ToggleAbility::getActivationEffects),
                    Codec.list(AbilityEffect.DIRECT_CODEC)
                            .optionalFieldOf("deactivation_effects", Collections.emptyList())
                            .forGetter(ToggleAbility::getDeactivationEffects)
            ).apply(instance, ToggleAbility::new));
    public static final float MIN_MANA = 0.5f;

    private final int warmupTime;
    private final int baseManaCost;
    private final boolean manaRequired;
    private final List<AbilityEffect> activationEffects;
    private final List<AbilityEffect> deactivationEffects;

    public ToggleAbility(ResourceLocation icon, Optional<ResourceLocation> smallIcon, int cooldown, int warmupTime,
            int manaCost, boolean manaRequired, List<AbilityEffect> activationEffects,
            List<AbilityEffect> deactivationEffects) {
        super(icon, smallIcon, cooldown);
        this.warmupTime = warmupTime;
        this.baseManaCost = manaCost;
        this.manaRequired = manaRequired;
        this.activationEffects = activationEffects;
        this.deactivationEffects = deactivationEffects;
    }

    @Override
    public boolean canActivate(AbilityContext context) {
        if (context.caster().getData(WotrAttachments.ABILITY_STATES).isActive(context.slot())) {
            // Can always deactivate
            return true;
        } else if (context.slot() != null
                && context.caster().getData(WotrAttachments.ABILITY_COOLDOWNS).isOnCooldown(context.slot())) {
            return false;
        } else {
            float manaCost = context.getAbilityAttribute(WotrAttributes.MANA_COST, getBaseManaCost());

            if (manaCost > 0) {
                ManaData manaData = context.caster().getData(WotrAttachments.MANA);
                return !(manaData.getAmount() < manaCost);
            }
            return true;
        }
    }

    @Override
    public void clientActivate(AbilityContext context) {
        context.caster().getData(WotrAttachments.ABILITY_STATES).setActive(context.slot(), true);
    }

    @Override
    public boolean activate(AbilityContext context) {
        if (context.caster().getData(WotrAttachments.ABILITY_STATES).isActive(context.slot())) {
            deactivationEffects.forEach(effect -> effect.apply(context.caster(), new ArrayList<>(), context));
            if (context.caster() instanceof ServerPlayer) {
                applyCooldown(context);
            }
            context.caster().getData(WotrAttachments.ABILITY_STATES).setActive(context.slot(), false);
            return true;
        } else {
            context.caster().getData(WotrAttachments.ABILITY_STATES).setActive(context.slot(), true);
            float manaCost = context.getAbilityAttribute(WotrAttributes.MANA_COST, getBaseManaCost());
            context.caster().getData(WotrAttachments.MANA).useAmount(manaCost);
            if (warmupTime == 0) {
                return tick(context, 0);
            }
            return false;
        }
    }

    @Override
    public boolean tick(AbilityContext context, long age) {
        if ((age - warmupTime) == 0) {
            activationEffects.forEach(effect -> effect.apply(context.caster(), new ArrayList<>(), context));
        }
        if (manaRequired && context.caster().getData(WotrAttachments.MANA).getAmount() < MIN_MANA) {
            deactivationEffects.forEach(effect -> effect.apply(context.caster(), new ArrayList<>(), context));
            context.caster().getData(WotrAttachments.ABILITY_STATES).setActive(context.slot(), false);
            applyCooldown(context);
            return true;
        }
        return false;
    }

    private void applyCooldown(AbilityContext context) {
        if (context.slot() != null) {
            context.caster()
                    .getData(WotrAttachments.ABILITY_COOLDOWNS)
                    .setCooldown(context.slot(),
                            (int) context.getAbilityAttribute(WotrAttributes.COOLDOWN, getBaseCooldown()));
        }
    }

    @Override
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
        for (AbilityEffect effect : activationEffects) {
            if (effect.isRelevant(modifierEffect)) {
                return true;
            }
        }
        for (AbilityEffect effect : deactivationEffects) {
            if (effect.isRelevant(modifierEffect)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getBaseManaCost() {
        return baseManaCost;
    }

    public boolean isManaRequired() {
        return manaRequired;
    }

    public int getWarmupTime() {
        return warmupTime;
    }

    public List<AbilityEffect> getActivationEffects() {
        return activationEffects;
    }

    public List<AbilityEffect> getDeactivationEffects() {
        return deactivationEffects;
    }

    @Override
    public MapCodec<? extends Ability> getCodec() {
        return CODEC;
    }
}
