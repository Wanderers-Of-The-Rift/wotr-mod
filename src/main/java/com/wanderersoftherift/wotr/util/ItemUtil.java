package com.wanderersoftherift.wotr.util;

import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class ItemUtil {

    private ItemUtil() {
    }

    public static List<ItemStack> condense(Collection<ItemStack> randomItems) {
        List<ItemStack> result = new ArrayList<>();
        for (ItemStack item : randomItems) {
            for (ItemStack existing : result) {
                if (existing.getCount() < existing.getMaxStackSize()
                        && ItemStack.isSameItemSameComponents(existing, item)) {
                    int amount = Math.min(existing.getMaxStackSize() - existing.getCount(), item.getCount());
                    existing.setCount(existing.getCount() + amount);
                    item.setCount(item.getCount() - amount);
                    if (item.isEmpty()) {
                        break;
                    }
                }
            }
            if (!item.isEmpty()) {
                result.add(item);
            }
        }
        return result;
    }
}
