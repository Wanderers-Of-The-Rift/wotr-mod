package com.wanderersoftherift.wotr.client;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.Ability;
import com.wanderersoftherift.wotr.abilities.attachment.AbilityEquipmentSlot;
import com.wanderersoftherift.wotr.abilities.attachment.AbilitySlots;
import com.wanderersoftherift.wotr.abilities.attachment.ManaData;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.init.WotrDataComponentType;
import com.wanderersoftherift.wotr.init.client.WotrKeyMappings;
import com.wanderersoftherift.wotr.item.ability.ActivatableAbility;
import com.wanderersoftherift.wotr.network.ability.SelectAbilitySlotPayload;
import com.wanderersoftherift.wotr.network.ability.UseAbilityPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import static com.wanderersoftherift.wotr.init.WotrEquipmentSlotTypes.VANILLA_SLOTS;
import static com.wanderersoftherift.wotr.init.client.WotrKeyMappings.*;

/**
 * Events related to abilities - key activation detection and mana ticking.
 */
@EventBusSubscriber(modid = WanderersOfTheRift.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public final class GearClientEvents {

    @SubscribeEvent
    public static void processGearBasic(InputEvent.InteractionKeyMappingTriggered event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.gameMode == null
                || minecraft.gameMode.getPlayerMode() == GameType.SPECTATOR) {
            return;
        }
        ItemStack item;
        if (minecraft.player.getItemInHand(InteractionHand.MAIN_HAND).has(WotrDataComponentType.ABILITY)) {
            item = minecraft.player.getItemInHand(InteractionHand.MAIN_HAND);
            if (event.isAttack()) {
                ActivatableAbility ability = item.get(WotrDataComponentType.ABILITY);
                ability.ability().value().onActivate(minecraft.player, item, null);
            }
        }

        /* Player player = Minecraft.getInstance().player;
        AbilitySlots abilitySlots = player.getData(WotrAttachments.ABILITY_SLOTS);
        for (int i = 0; i < WotrKeyMappings.ABILITY_SLOT_KEYS.size(); i++) {
            while (ABILITY_SLOT_KEYS.get(i).consumeClick()) {
                useAbilitySlot(abilitySlots, i, player);
            }
        }

        boolean selectionUpdated = false;
        while (PREV_ABILITY_KEY.consumeClick()) {
            abilitySlots.decrementSelected();
            selectionUpdated = true;
        }
        while (NEXT_ABILITY_KEY.consumeClick()) {
            abilitySlots.incrementSelected();
            selectionUpdated = true;
        }
        while (USE_ABILITY_KEY.consumeClick()) {
            int slot = abilitySlots.getSelectedSlot();
            useAbilitySlot(abilitySlots, slot, player);
            selectionUpdated = false; // Because using a slot selected the slot
        }
        if (selectionUpdated) {
            PacketDistributor.sendToServer(new SelectAbilitySlotPayload(abilitySlots.getSelectedSlot()));
        } */
    }

    @SubscribeEvent
    public static void tickMana(ClientTickEvent.Pre event) {
        Player player = Minecraft.getInstance().player;
        if (player == null || Minecraft.getInstance().isPaused()) {
            return;
        }

        ManaData manaData = player.getData(WotrAttachments.MANA);
        manaData.tick();
    }

}
