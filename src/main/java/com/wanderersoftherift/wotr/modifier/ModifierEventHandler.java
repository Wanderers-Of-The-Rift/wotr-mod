package com.wanderersoftherift.wotr.modifier;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.inventory.slot.WotrEquipmentSlot;
import com.wanderersoftherift.wotr.core.inventory.slot.WotrEquipmentSlotEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

/**
 * Handles events to trigger modifier updates
 */
@EventBusSubscriber(modid = WanderersOfTheRift.MODID, bus = EventBusSubscriber.Bus.GAME)
public class ModifierEventHandler {

    @SubscribeEvent
    public static void onSlotChanged(WotrEquipmentSlotEvent.Changed event) {
        disableItem(event.getEntity(), event.getSlot(), event.getFrom());
        enableItem(event.getEntity(), event.getSlot(), event.getTo());
    }

    private static void enableItem(LivingEntity entity, WotrEquipmentSlot slot, ItemStack stack) {
        if (slot.canAccept(stack)) {
            visitItemModifierProviders(stack, slot, entity,
                    (modifierHolder, tier, roll, source) -> modifierHolder.value()
                            .enableModifier(roll, entity, source, tier));
        }
    }

    private static void disableItem(LivingEntity entity, WotrEquipmentSlot slot, ItemStack stack) {
        if (slot.canAccept(stack)) {
            visitItemModifierProviders(stack, slot, entity,
                    (modifierHolder, tier, roll, source) -> modifierHolder.value()
                            .disableModifier(roll, entity, source, tier));
        }
    }

    private static void visitItemModifierProviders(
            ItemStack stack,
            WotrEquipmentSlot slot,
            LivingEntity entity,
            ModifierProvider.Action action) {
        stack.getAllOfType(ModifierProvider.class).forEach(x -> x.forEachModifier(stack, slot, entity, action));
    }

}
