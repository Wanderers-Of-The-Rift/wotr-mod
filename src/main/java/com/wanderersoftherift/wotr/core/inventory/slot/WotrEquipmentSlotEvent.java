package com.wanderersoftherift.wotr.core.inventory.slot;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;

/**
 * Events relating to WotrEquipmentSlots
 */
public abstract class WotrEquipmentSlotEvent extends Event {
    private final LivingEntity entity;
    private final WotrEquipmentSlot slot;

    public WotrEquipmentSlotEvent(LivingEntity entity, WotrEquipmentSlot slot) {
        this.entity = entity;
        this.slot = slot;
    }

    public WotrEquipmentSlot getSlot() {
        return slot;
    }

    public LivingEntity getEntity() {
        return entity;
    }

    /**
     * This event is sent when the contents of a WotrEquipmentSlot change
     */
    public static class Changed extends WotrEquipmentSlotEvent {

        private final ItemStack from;
        private final ItemStack to;

        public Changed(LivingEntity entity, WotrEquipmentSlot slot, ItemStack from, ItemStack to) {
            super(entity, slot);
            this.from = from;
            this.to = to;
        }

        public ItemStack getFrom() {
            return from;
        }

        public ItemStack getTo() {
            return to;
        }
    }
}
