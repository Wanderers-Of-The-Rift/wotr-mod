package com.wanderersoftherift.wotr.gui.menu.slot;

import com.wanderersoftherift.wotr.init.WotrItems;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * A slot that can only hold skill thread
 */
public class SkillThreadSlot extends Slot {

    public SkillThreadSlot(Container container, int slot, int x, int y) {
        super(container, slot, x, y);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return stack.is(WotrItems.SKILL_THREAD.get());
    }

}
