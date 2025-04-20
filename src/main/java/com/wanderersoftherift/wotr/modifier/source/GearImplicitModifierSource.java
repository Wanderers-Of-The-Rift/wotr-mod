package com.wanderersoftherift.wotr.modifier.source;

import com.wanderersoftherift.wotr.item.implicit.GearImplicits;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;

public class GearImplicitModifierSource implements ModifierSource {
    private final GearImplicits implicits;
    private final EquipmentSlot slot;
    private final Entity entity;

    public GearImplicitModifierSource(GearImplicits implicits, EquipmentSlot slot, Entity entity) {
        this.implicits = implicits;
        this.slot = slot;
        this.entity = entity;
    }

    @Override
    public String getSerializedName() {
        return "implicits_" + slot.getSerializedName();
    }
}
