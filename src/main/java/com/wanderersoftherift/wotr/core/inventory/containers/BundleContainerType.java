package com.wanderersoftherift.wotr.core.inventory.containers;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Container type for supporting bundles
 */
public class BundleContainerType implements ContainerType {
    @Override
    public boolean isContainer(ItemStack item) {
        return item.has(DataComponents.BUNDLE_CONTENTS);
    }

    @Override
    public ContainerWrapper getWrapper(ItemStack item) {
        return new BundleComponentContainerWrapper(item);
    }

    private static class BundleComponentContainerWrapper implements ContainerWrapper {
        private final ItemStack containerItem;
        private final List<ContainerItemWrapper> contents;

        public BundleComponentContainerWrapper(ItemStack item) {
            this.containerItem = item;
            contents = new ArrayList<>();
            for (ItemStack itemCopy : item.get(DataComponents.BUNDLE_CONTENTS).itemsCopy()) {
                contents.add(new DirectContainerItemWrapper(itemCopy));
            }
        }

        @Override
        public void recordChanges() {
            containerItem.set(DataComponents.BUNDLE_CONTENTS,
                    new BundleContents(contents.stream()
                            .map(ContainerItemWrapper::getReadOnlyItemStack)
                            .filter(x -> !x.isEmpty())
                            .toList()));
        }

        @Override
        public @NotNull Iterator<ContainerItemWrapper> iterator() {
            return contents.iterator();
        }
    }
}
