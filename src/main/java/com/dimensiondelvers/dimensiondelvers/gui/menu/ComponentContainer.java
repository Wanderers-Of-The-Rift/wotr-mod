package com.dimensiondelvers.dimensiondelvers.gui.menu;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;

import java.util.ArrayList;
import java.util.List;

public class ComponentContainer extends SimpleContainer {
    private final ItemStack stack;

    public ComponentContainer(ItemStack stack) {
        super(9); // 9 slots for your bag
        this.stack = stack;

        ItemContainerContents contents = stack.get(DataComponents.CONTAINER);
        if (contents != null) {
            List<ItemStack> items = contents.stream().toList();
            for (int i = 0; i < Math.min(items.size(), getContainerSize()); i++) {
                setItem(i, items.get(i));
            }
        }
    }

    @Override
    public void setChanged() {
        super.setChanged();
        List<ItemStack> items = new ArrayList<>();
        for (int i = 0; i < getContainerSize(); i++) {
            items.add(getItem(i));
        }
        stack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(items));
    }
}
