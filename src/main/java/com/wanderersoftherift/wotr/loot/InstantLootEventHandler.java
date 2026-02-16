package com.wanderersoftherift.wotr.loot;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ItemStackedOnOtherEvent;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;

@EventBusSubscriber
public class InstantLootEventHandler {

    @SubscribeEvent
    public static void handlePickup(ItemEntityPickupEvent.Pre event) {
        InstantLoot.tryConsume(event.getItemEntity().getItem(), event.getPlayer());
    }

    @SubscribeEvent
    public static void handleMerge(ItemStackedOnOtherEvent event) {
        if (InstantLoot.tryConsume(
                event.getCarriedItem() /* should be getStackedOnItem but someone@neoforge swapped them around */,
                event.getPlayer())) {
            event.setCanceled(true);
        }
    }
}
