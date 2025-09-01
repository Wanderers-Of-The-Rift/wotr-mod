package com.wanderersoftherift.wotr.modifier;

import com.wanderersoftherift.wotr.core.inventory.slot.WotrEquipmentSlot;
import com.wanderersoftherift.wotr.core.inventory.slot.WotrEquipmentSlotFromMC;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.common.NeoForge;

import java.util.ArrayList;
import java.util.List;

public class ModifierHelper {

    public static void enableAllEquipment(LivingEntity entity) {
        visitEquipmentModifierProviders(entity, (modifierHolder, tier, roll, source) -> modifierHolder.value()
                .enableModifier(roll, entity, source, tier));
    }

    public static void enableItem(LivingEntity entity, EquipmentSlot slot) {
        enableItem(entity, WotrEquipmentSlotFromMC.fromVanillaSlot(slot));
    }

    public static void enableItem(LivingEntity entity, WotrEquipmentSlot slot) {
        enableItem(entity, slot, slot.getContent(entity));
    }

    public static void enableItem(LivingEntity entity, EquipmentSlot slot, ItemStack stack) {
        enableItem(entity, WotrEquipmentSlotFromMC.fromVanillaSlot(slot), stack);
    }

    public static void enableItem(LivingEntity entity, WotrEquipmentSlot slot, ItemStack stack) {
        if (slot.canAccept(stack)) {
            visitItemModifierProviders(stack, slot, entity,
                    (modifierHolder, tier, roll, source) -> modifierHolder.value()
                            .enableModifier(roll, entity, source, tier));
        }
    }

    public static void disableAllEquipment(LivingEntity entity) {
        visitEquipmentModifierProviders(entity, (modifierHolder, tier, roll, source) -> modifierHolder.value()
                .disableModifier(roll, entity, source, tier));
    }

    public static void disableItem(LivingEntity entity, EquipmentSlot slot) {
        disableItem(entity, WotrEquipmentSlotFromMC.fromVanillaSlot(slot));
    }

    public static void disableItem(LivingEntity entity, WotrEquipmentSlot slot) {
        disableItem(entity, slot, slot.getContent(entity));
    }

    public static void disableItem(LivingEntity entity, EquipmentSlot slot, ItemStack stack) {
        disableItem(entity, WotrEquipmentSlotFromMC.fromVanillaSlot(slot), stack);
    }

    public static void disableItem(LivingEntity entity, WotrEquipmentSlot slot, ItemStack stack) {
        if (slot.canAccept(stack)) {
            visitItemModifierProviders(stack, slot, entity,
                    (modifierHolder, tier, roll, source) -> modifierHolder.value()
                            .disableModifier(roll, entity, source, tier));
        }
    }

    private static void visitEquipmentModifierProviders(LivingEntity entity, ModifierProvider.Action action) {
        var slots = NeoForge.EVENT_BUS.post(new CollectEquipmentSlotsEvent(new ArrayList<>(), entity)).getSlots();
        for (var slot : slots) {
            visitItemModifierProviders(slot.getContent(entity), slot, entity, action);
        }
    }

    private static void visitItemModifierProviders(
            ItemStack stack,
            WotrEquipmentSlot slot,
            LivingEntity entity,
            ModifierProvider.Action action) {
        stack.getAllOfType(ModifierProvider.class).forEach(x -> x.forEachModifier(stack, slot, entity, action));
    }

    public static class CollectEquipmentSlotsEvent extends Event {
        private final List<WotrEquipmentSlot> slots;
        private final LivingEntity entity;

        public CollectEquipmentSlotsEvent(List<WotrEquipmentSlot> slots, LivingEntity entity) {
            this.slots = slots;
            this.entity = entity;
        }

        public List<WotrEquipmentSlot> getSlots() {
            return slots;
        }

        public LivingEntity getEntity() {
            return entity;
        }
    }
}
