package com.wanderersoftherift.wotr.abilities;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.attachment.ManaData;
import com.wanderersoftherift.wotr.abilities.effects.AbilityEffect;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.init.WotrAttributes;
import com.wanderersoftherift.wotr.init.WotrDataComponentType;
import com.wanderersoftherift.wotr.item.ability.Cooldown;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

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

    public StandardAbility(ResourceLocation icon, Optional<ResourceLocation> smallIcon, int baseCooldown, int manaCost,
            List<AbilityEffect> effects) {
        super(icon, smallIcon, effects, baseCooldown);
        setBaseManaCost(manaCost);
    }

    @Override
    public MapCodec<? extends Ability> getCodec() {
        return CODEC;
    }

    @Override
    public boolean onActivate(LivingEntity caster, ItemStack abilityItem) {
        if (!this.canUse(caster)) {
            if (caster instanceof ServerPlayer player) {
                // TODO: Proper translatable component, or maybe we remove this?
                player.displayClientMessage(Component.literal("You cannot use this"), true);
            }
            return false;
        }
        if (abilityItem.getOrDefault(WotrDataComponentType.COOLDOWN, new Cooldown()).onCooldown(caster.level())) {
            return false;
        }
        AbilityContext abilityContext = new AbilityContext(caster, abilityItem);
        abilityContext.enableModifiers();
        try {
            int manaCost = (int) abilityContext.getAbilityAttribute(WotrAttributes.MANA_COST, getBaseManaCost());
            ManaData manaData = caster.getData(WotrAttachments.MANA);
            if (manaCost > 0) {
                if (manaData.getAmount() < manaCost) {
                    return false;
                }
            }
            // TODO: Attachment holder pattern on mana pool
            if (caster instanceof ServerPlayer player) {
                manaData.useAmount(player, manaCost);
                this.getEffects().forEach(effect -> effect.apply(player, new ArrayList<>(), abilityContext));
            }
            long time = caster.level().getGameTime();
            Cooldown cooldown = new Cooldown(time,
                    time + (int) abilityContext.getAbilityAttribute(WotrAttributes.COOLDOWN, getBaseCooldown()));
            abilityItem.set(WotrDataComponentType.COOLDOWN, cooldown);
        } finally {
            abilityContext.disableModifiers();
        }
        return true;
    }
}
