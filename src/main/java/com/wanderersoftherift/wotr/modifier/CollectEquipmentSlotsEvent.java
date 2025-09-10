package com.wanderersoftherift.wotr.modifier;

import com.wanderersoftherift.wotr.core.inventory.slot.WotrEquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.Event;

import java.util.List;

/**
 * Event to obtain a list of all equipment slots relevant to the provided entity
 */
public class CollectEquipmentSlotsEvent extends Event {
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
