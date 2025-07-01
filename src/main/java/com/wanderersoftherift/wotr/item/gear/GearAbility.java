package com.wanderersoftherift.wotr.item.gear;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import com.wanderersoftherift.wotr.abilities.AbstractAbility;
import com.wanderersoftherift.wotr.abilities.StandardAbility;
import com.wanderersoftherift.wotr.abilities.attachment.ManaData;
import com.wanderersoftherift.wotr.abilities.effects.AbstractEffect;
import com.wanderersoftherift.wotr.codec.LaxRegistryCodec;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.init.WotrAttributes;
import com.wanderersoftherift.wotr.network.UseAbilityPayload;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.wanderersoftherift.wotr.init.WotrRegistries.Keys.ABILITIES;
import static com.wanderersoftherift.wotr.init.WotrRegistries.Keys.ABILITIES_GEAR;

public class GearAbility extends AbstractAbility {
    public static ItemStack gear;

    public static final MapCodec<GearAbility> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance
                    .group(ResourceLocation.CODEC.fieldOf("ability_name").forGetter(GearAbility::getName),
                            ResourceLocation.CODEC.fieldOf("icon").forGetter(GearAbility::getIcon),
                            Codec.INT.fieldOf("cooldown").forGetter(ability -> (int) ability.getBaseCooldown()),
                            Codec.INT.optionalFieldOf("mana_cost", 0).forGetter(GearAbility::getBaseManaCost),
                            Codec.list(AbstractEffect.DIRECT_CODEC)
                                    .optionalFieldOf("effects", Collections.emptyList())
                                    .forGetter(GearAbility::getEffects)
                    ).apply(instance, GearAbility::new));

    public GearAbility(ResourceLocation resourceLocation, ResourceLocation icon, int baseCooldown, int manaCost,
                           List<AbstractEffect> effects) {

        super(resourceLocation, icon, effects, baseCooldown);
        setBaseManaCost(manaCost);
    }

    @Override
    public MapCodec<? extends AbstractAbility> getCodec() {
        return CODEC;
    }

    @Override
    public void onActivate(Player player, int slot, ItemStack abilityItem){
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
            int manaCost = (int) abilityContext.getAbilityAttribute(WotrAttributes.MANA_COST, getBaseManaCost());
            ManaData manaData = player.getData(WotrAttachments.MANA);
            if (manaCost > 0) {
                if (manaData.getAmount() < manaCost) {
                    return;
                }
            }
            if (player instanceof ServerPlayer) {
                manaData.useAmount(player, manaCost);
                this.getEffects().forEach(effect -> effect.apply(player, new ArrayList<>(), abilityContext));
                this.setCooldown(player, slot,
                        abilityContext.getAbilityAttribute(WotrAttributes.COOLDOWN, getBaseCooldown()));
            } else {
                PacketDistributor.sendToServer(new UseAbilityPayload(slot));
            }
        } finally {
            abilityContext.disableModifiers();
        }
    }

    @Override
    public void onDeactivate(Player player, int slot){

    }

    @Override
    public void tick(Player player){

    }
}
