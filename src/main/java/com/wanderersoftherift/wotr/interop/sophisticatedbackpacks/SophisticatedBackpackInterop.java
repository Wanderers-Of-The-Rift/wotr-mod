package com.wanderersoftherift.wotr.interop.sophisticatedbackpacks;

import com.google.common.collect.Iterators;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.inventory.ItemAccessor;
import com.wanderersoftherift.wotr.core.inventory.containers.ContainerType;
import com.wanderersoftherift.wotr.core.inventory.containers.ContainerWrapper;
import com.wanderersoftherift.wotr.core.inventory.containers.DirectItemAccessor;
import com.wanderersoftherift.wotr.core.inventory.containers.ItemStackHandlerContainers;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackItem;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.IBackpackWrapper;
import net.p3pp3rf1y.sophisticatedcore.inventory.InventoryHandler;
import net.p3pp3rf1y.sophisticatedcore.upgrades.UpgradeHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

public final class SophisticatedBackpackInterop {
    private SophisticatedBackpackInterop() {
    }

    public static void load() {

    }

    public static void register(RegisterEvent event) {
        event.register(WotrRegistries.Keys.CONTAINER_TYPES, registry -> registry
                .register(WanderersOfTheRift.id("sophisticated_storage_backpack"), new SophisticatedBackpackType()));
    }

    private static class SophisticatedBackpackType implements ContainerType {

        @Override
        public boolean isContainer(ItemStack item) {
            return item.getItem() instanceof BackpackItem;
        }

        @Override
        public ContainerWrapper getWrapper(ItemStack item) {
            return new SophisticatedBackpackWrapper(item);
        }

        private static class SophisticatedBackpackWrapper implements ContainerWrapper {

            private final IBackpackWrapper backpackWrapper;
            private final ItemAccessor containerItem;

            public SophisticatedBackpackWrapper(ItemStack item) {
                this(new DirectItemAccessor(item));
            }

            public SophisticatedBackpackWrapper(ItemAccessor item) {
                this.backpackWrapper = BackpackWrapper.fromStack(item.getReadOnlyItemStack());
                this.containerItem = item;
            }

            @Override
            public void recordChanges() {

            }

            @Override
            public @Nullable ItemAccessor containerItem() {
                return containerItem;
            }

            @Override
            public @NotNull Iterator<ItemAccessor> iterator() {
                InventoryHandler inventory = backpackWrapper.getInventoryHandler();
                UpgradeHandler upgrades = backpackWrapper.getUpgradeHandler();
                return Iterators.concat(ItemStackHandlerContainers.iterateNonEmpty(inventory),
                        ItemStackHandlerContainers.iterateNonEmpty(upgrades));
            }
        }

    }
}
