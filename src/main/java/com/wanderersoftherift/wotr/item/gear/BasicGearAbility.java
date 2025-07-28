package com.wanderersoftherift.wotr.item.gear;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.effects.AbstractEffect;
import com.wanderersoftherift.wotr.network.UseGearAbilityPayload;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BasicGearAbility extends AbstractGearAbility {
    public static final MapCodec<BasicGearAbility> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance
                    .group(ResourceLocation.CODEC.fieldOf("ability_name").forGetter(BasicGearAbility::getName),
                            Item.CODEC.fieldOf("gear").forGetter(BasicGearAbility::getItem),
                            ResourceLocation.CODEC.fieldOf("type").forGetter(BasicGearAbility::getType),
                            Codec.list(AbstractEffect.DIRECT_CODEC)
                                    .optionalFieldOf("effects", Collections.emptyList())
                                    .forGetter(BasicGearAbility::getEffects)
                    ).apply(instance, BasicGearAbility::new));

    public BasicGearAbility(ResourceLocation resourceLocation, Holder<Item> gear, ResourceLocation type,
            List<AbstractEffect> effects) {
        super(resourceLocation, gear, effects, type);
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
    public void onDeactivate(Player player) {
    }
}
