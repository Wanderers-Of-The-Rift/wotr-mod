package com.wanderersoftherift.wotr.item.gear;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import com.wanderersoftherift.wotr.abilities.AbstractAbility;
import com.wanderersoftherift.wotr.abilities.StandardAbility;
import com.wanderersoftherift.wotr.abilities.effects.AbstractEffect;
import com.wanderersoftherift.wotr.init.WotrAttributes;
import com.wanderersoftherift.wotr.network.UseAbilityPayload;
import com.wanderersoftherift.wotr.network.UseGearAbilityPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BasicGearAbility extends AbstractGearAbility {
    public static final MapCodec<BasicGearAbility> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance
                    .group(ResourceLocation.CODEC.fieldOf("ability_name").forGetter(BasicGearAbility::getName),
                            ResourceLocation.CODEC.fieldOf("gear").forGetter(BasicGearAbility::getItem),
                            Codec.list(AbstractEffect.DIRECT_CODEC)
                                    .optionalFieldOf("effects", Collections.emptyList())
                                    .forGetter(BasicGearAbility::getEffects)
                    ).apply(instance, BasicGearAbility::new));

    public BasicGearAbility(ResourceLocation resourceLocation, ItemStack gear, List<AbstractEffect> effects) {
        super(resourceLocation, gear, effects);
    }
    @Override
    public MapCodec<? extends AbstractGearAbility> getCodec() {
        return CODEC;
    }

    @Override
    public void onActivate(Player player, ItemStack gear) {
        GearAbilityContext abilityContext = new GearAbilityContext(player, gear, true);
        abilityContext.enableModifiers();
        try {
            if (player instanceof ServerPlayer) {
                this.getEffects().forEach(effect -> effect.apply(player, new ArrayList<>(), abilityContext));
            } else {
                PacketDistributor.sendToServer(new UseGearAbilityPayload(true));
            }
        } finally {
            abilityContext.disableModifiers();
        }
    }

    @Override
    public void onDeactivate(Player player) {}
}
