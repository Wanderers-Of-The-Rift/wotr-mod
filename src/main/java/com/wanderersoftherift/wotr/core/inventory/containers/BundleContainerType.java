package com.wanderersoftherift.wotr.core.inventory.containers;

import com.wanderersoftherift.wotr.core.inventory.ItemAccessor;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
        private final ItemAccessor containerItem;
        private final List<ItemAccessor> contents;

        public BundleComponentContainerWrapper(ItemStack item) {
            this(new DirectItemAccessor(item));
        }

        public BundleComponentContainerWrapper(ItemAccessor item) {
            this.containerItem = item;
            contents = new ArrayList<>();
            for (ItemStack itemCopy : item.getReadOnlyItemStack().get(DataComponents.BUNDLE_CONTENTS).itemsCopy()) {
                contents.add(new DirectItemAccessor(itemCopy));
            }
        }

        @Override
        public void recordChanges() {
            containerItem.applyComponents(DataComponentPatch.builder()
                    .set(DataComponents.BUNDLE_CONTENTS,
                            new BundleContents(contents.stream()
                                    .map(ItemAccessor::getReadOnlyItemStack)
                                    .filter(x -> !x.isEmpty())
                                    .toList()))
                    .build());
        }

        @Override
        public @Nullable ItemAccessor containerItem() {
            return containerItem;
        }

        @Override
        public @NotNull Iterator<ItemAccessor> iterator() {
            return contents.iterator();
        }
    }
}
