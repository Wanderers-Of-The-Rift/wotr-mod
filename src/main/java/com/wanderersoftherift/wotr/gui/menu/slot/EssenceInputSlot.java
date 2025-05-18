package com.wanderersoftherift.wotr.gui.menu.slot;

import com.wanderersoftherift.wotr.init.WotrDataMaps;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * Input slot that can only hold items with essence values
 */
public class EssenceInputSlot extends Slot {
    public EssenceInputSlot(Container container, int slot, int x, int y) {
        super(container, slot, x, y);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return stack.getItemHolder().getData(WotrDataMaps.ESSENCE_VALUE_DATA) != null;
    }

}
