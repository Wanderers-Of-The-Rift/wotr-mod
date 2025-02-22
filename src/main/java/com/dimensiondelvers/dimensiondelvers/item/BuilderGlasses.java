package com.dimensiondelvers.dimensiondelvers.item;

import com.dimensiondelvers.dimensiondelvers.init.ModItems;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;

public class BuilderGlasses extends Item {
    public BuilderGlasses() {
        super(
                new Properties()
                        .setId(ModItems.BUILDER_GLASSES.getKey())
                        .equippable(EquipmentSlot.HEAD)
                        .stacksTo(1)
        );
    }
}
