package com.wanderersoftherift.wotr.modifier.source;

import com.wanderersoftherift.wotr.item.implicit.GearImplicits;
import com.wanderersoftherift.wotr.modifier.WotrEquipmentSlot;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

public class GearImplicitModifierSource implements ModifierSource {
    private final GearImplicits implicits;
    private final WotrEquipmentSlot slot;
    private final Entity entity;

    public GearImplicitModifierSource(GearImplicits implicits, WotrEquipmentSlot slot, Entity entity) {
        this.implicits = implicits;
        this.slot = slot;
        this.entity = entity;
    }

    @Override
    public String getSerializedName() {
        return "implicits_" + slot.getSerializedName();
    }

    @Override
    public @Nullable WotrEquipmentSlot slot() {
        return slot;
    }
}
