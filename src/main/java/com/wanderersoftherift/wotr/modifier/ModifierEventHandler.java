package com.wanderersoftherift.wotr.modifier;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.inventory.slot.WotrEquipmentSlotEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

/**
 * Handles events to trigger modifier updates
 */
@EventBusSubscriber(modid = WanderersOfTheRift.MODID, bus = EventBusSubscriber.Bus.GAME)
public class ModifierEventHandler {

    @SubscribeEvent
    public static void onSlotChanged(WotrEquipmentSlotEvent.Changed event) {
        ModifierHelper.disableItem(event.getEntity(), event.getSlot(), event.getFrom());
        ModifierHelper.enableItem(event.getEntity(), event.getSlot(), event.getTo());
    }
}
