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

    public static void runIterationOnItem(
            ItemStack stack,
            WotrEquipmentSlot slot,
            LivingEntity entity,
            ModifierProvider.Action action) {
        stack.getAllOfType(ModifierProvider.class).forEach(x -> x.forEachModifier(stack, slot, entity, action));
    }

    public static void runIterationOnEquipment(LivingEntity entity, ModifierProvider.Action action) {
        var slots = NeoForge.EVENT_BUS.post(new CollectEquipmentSlotsEvent(new ArrayList<>(), entity)).getSlots();
        for (var wotrSlot : slots) {
            runIterationOnItem(wotrSlot.getContent(entity), wotrSlot, entity, action);
        }
    }

    public static void enableModifier(LivingEntity entity) {
        runIterationOnEquipment(entity, (modifierHolder, tier, roll, source) -> modifierHolder.value()
                .enableModifier(roll, entity, source, tier));
    }

    public static void enableModifier(ItemStack stack, LivingEntity entity, EquipmentSlot slot) {
        enableModifier(stack, entity, WotrEquipmentSlotFromMC.fromVanillaSlot(slot));
    }

    public static void enableModifier(ItemStack stack, LivingEntity entity, WotrEquipmentSlot slot) {
        runIterationOnItem(stack, slot, entity, (modifierHolder, tier, roll, source) -> modifierHolder.value()
                .enableModifier(roll, entity, source, tier));
    }

    public static void disableModifier(LivingEntity entity) {
        runIterationOnEquipment(entity, (modifierHolder, tier, roll, source) -> modifierHolder.value()
                .disableModifier(roll, entity, source, tier));
    }

    public static void disableModifier(ItemStack stack, LivingEntity entity, EquipmentSlot slot) {
        disableModifier(stack, entity, WotrEquipmentSlotFromMC.fromVanillaSlot(slot));
    }

    public static void disableModifier(ItemStack stack, LivingEntity entity, WotrEquipmentSlot slot) {
        runIterationOnItem(stack, slot, entity, (modifierHolder, tier, roll, source) -> modifierHolder.value()
                .disableModifier(roll, entity, source, tier));
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
