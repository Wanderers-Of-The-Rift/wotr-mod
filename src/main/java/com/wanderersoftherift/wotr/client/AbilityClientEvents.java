package com.wanderersoftherift.wotr.client;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.Ability;
import com.wanderersoftherift.wotr.abilities.attachment.AbilitySlots;
import com.wanderersoftherift.wotr.abilities.attachment.ManaData;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.init.WotrDataComponentType;
import com.wanderersoftherift.wotr.init.client.WotrKeyMappings;
import com.wanderersoftherift.wotr.network.ability.SelectAbilitySlotPayload;
import com.wanderersoftherift.wotr.network.ability.UseAbilityPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import static com.wanderersoftherift.wotr.init.client.WotrKeyMappings.ABILITY_SLOT_KEYS;
import static com.wanderersoftherift.wotr.init.client.WotrKeyMappings.ACTIVATE_ABILITY_SCROLL;
import static com.wanderersoftherift.wotr.init.client.WotrKeyMappings.NEXT_ABILITY_KEY;
import static com.wanderersoftherift.wotr.init.client.WotrKeyMappings.PREV_ABILITY_KEY;
import static com.wanderersoftherift.wotr.init.client.WotrKeyMappings.USE_ABILITY_KEY;

/**
 * Events related to abilities - key activation detection and mana ticking.
 */
@EventBusSubscriber(modid = WanderersOfTheRift.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public final class AbilityClientEvents {

    @SubscribeEvent
    public static void processScrollWheelForAbilityBar(InputEvent.MouseScrollingEvent event) {
        if (ACTIVATE_ABILITY_SCROLL.isDown()) {
            int scrollDelta = (int) event.getScrollDeltaY();

            int direction = Integer.signum(scrollDelta) * -1;

            if (direction != 0) {
                Player player = Minecraft.getInstance().player;
                AbilitySlots abilitySlots = player.getData(WotrAttachments.ABILITY_SLOTS);
                int selectedSlot = abilitySlots.getSelectedSlot();

                abilitySlots.setSelectedSlot(Math.floorMod(selectedSlot + direction, AbilitySlots.ABILITY_BAR_SIZE));

                PacketDistributor.sendToServer(new SelectAbilitySlotPayload(abilitySlots.getSelectedSlot()));
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void processAbilityKeys(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.gameMode == null
                || minecraft.gameMode.getPlayerMode() == GameType.SPECTATOR) {
            return;
        }

        Player player = Minecraft.getInstance().player;
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
        }
    }

    private static void useAbilitySlot(AbilitySlots abilitySlots, int slot, Player player) {
        ItemStack abilityItem = abilitySlots.getStackInSlot(slot);
        Holder<Ability> ability = abilityItem.get(WotrDataComponentType.ABILITY);
        if (ability != null && ability.value().onActivate(player, abilityItem)) {
            PacketDistributor.sendToServer(new UseAbilityPayload(slot));
        }
        abilitySlots.setSelectedSlot(slot);
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
