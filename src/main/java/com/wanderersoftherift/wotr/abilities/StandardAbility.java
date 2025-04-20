package com.wanderersoftherift.wotr.abilities;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.attachment.ManaData;
import com.wanderersoftherift.wotr.abilities.effects.AbstractEffect;
import com.wanderersoftherift.wotr.init.ModAttachments;
import com.wanderersoftherift.wotr.init.ModAttributes;
import com.wanderersoftherift.wotr.network.UseAbilityPayload;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StandardAbility extends AbstractAbility {

    public static final MapCodec<StandardAbility> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance
                    .group(ResourceLocation.CODEC.fieldOf("ability_name").forGetter(StandardAbility::getName),
                            ResourceLocation.CODEC.fieldOf("icon").forGetter(StandardAbility::getIcon),
                            Codec.INT.fieldOf("cooldown").forGetter(ability -> (int) ability.getBaseCooldown()),
                            Codec.INT.optionalFieldOf("mana_cost", 0).forGetter(StandardAbility::getBaseManaCost),
                            Codec.list(AbstractEffect.DIRECT_CODEC)
                                    .optionalFieldOf("effects", Collections.emptyList())
                                    .forGetter(StandardAbility::getEffects))
                    .apply(instance, StandardAbility::new));

    public StandardAbility(ResourceLocation resourceLocation, ResourceLocation icon, int baseCooldown, int manaCost,
            List<AbstractEffect> effects) {
        super(resourceLocation, icon, effects, baseCooldown);
        setBaseManaCost(manaCost);
    }

    @Override
    public MapCodec<? extends AbstractAbility> getCodec() {
        return CODEC;
    }

    @Override
    public void onActivate(Player player, int slot, ItemStack abilityItem) {
        if (!this.canPlayerUse(player)) {
            player.displayClientMessage(Component.literal("You cannot use this"), true);
            return;
        }
        if (this.isOnCooldown(player, slot)) {
            return;
        }
        AbilityContext abilityContext = new AbilityContext(player, abilityItem);
        abilityContext.enableModifiers();
        try {
            int manaCost = (int) abilityContext.getAbilityAttribute(ModAttributes.MANA_COST, getBaseManaCost());
            ManaData manaData = player.getData(ModAttachments.MANA);
            if (manaCost > 0) {
                if (manaData.getAmount() < manaCost) {
                    return;
                }
            }
            if (player instanceof ServerPlayer) {
                manaData.useAmount(player, manaCost);
                this.getEffects().forEach(effect -> effect.apply(player, new ArrayList<>(), abilityContext));
                this.setCooldown(player, slot,
                        abilityContext.getAbilityAttribute(ModAttributes.COOLDOWN, getBaseCooldown()));
            } else {
                PacketDistributor.sendToServer(new UseAbilityPayload(slot));
            }
        } finally {
            abilityContext.disableModifiers();
        }
    }

    @Override
    public void onDeactivate(Player player, int slot) {

    }

    @Override
    public void tick(Player player) {

    }
}
