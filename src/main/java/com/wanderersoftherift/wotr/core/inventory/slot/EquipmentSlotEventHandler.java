package com.wanderersoftherift.wotr.core.inventory.slot;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;

@EventBusSubscriber(modid = WanderersOfTheRift.MODID, bus = EventBusSubscriber.Bus.GAME)
public final class EquipmentSlotEventHandler {

    @SubscribeEvent
    public static void onEntitySlotChanged(LivingEquipmentChangeEvent event) {
        NeoForge.EVENT_BUS.post(new WotrEquipmentSlotEvent.Changed(event.getEntity(),
                WotrEquipmentSlotFromMC.fromVanillaSlot(event.getSlot()), event.getFrom(), event.getTo()));
    }

}
