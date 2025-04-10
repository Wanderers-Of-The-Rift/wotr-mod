package com.wanderersoftherift.wotr.interop.sophisticatedbackpacks;

import com.google.common.collect.Iterators;
import com.wanderersoftherift.wotr.server.inventorySnapshot.InventorySnapshotSystem;
import com.wanderersoftherift.wotr.server.inventorySnapshot.containers.ContainerItemWrapper;
import com.wanderersoftherift.wotr.server.inventorySnapshot.containers.ContainerType;
import com.wanderersoftherift.wotr.server.inventorySnapshot.containers.ContainerWrapper;
import com.wanderersoftherift.wotr.server.inventorySnapshot.containers.ItemStackHandlerContainers;
import net.minecraft.world.item.ItemStack;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackItem;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.IBackpackWrapper;
import net.p3pp3rf1y.sophisticatedcore.inventory.InventoryHandler;
import net.p3pp3rf1y.sophisticatedcore.upgrades.UpgradeHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public final class SophisticatedBackpackInterop {
    private SophisticatedBackpackInterop() {
    }

    public static void load() {
        InventorySnapshotSystem.getInstance().registerContainerStrategy(new SophisticatedBackpackType());
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

            private IBackpackWrapper backpackWrapper;

            public SophisticatedBackpackWrapper(ItemStack item) {
                this.backpackWrapper = BackpackWrapper.fromStack(item);
            }

            @Override
            public void recordChanges() {
                // NOOP
            }

            @Override
            public @NotNull Iterator<ContainerItemWrapper> iterator() {
                InventoryHandler inventory = backpackWrapper.getInventoryHandler();
                UpgradeHandler upgrades = backpackWrapper.getUpgradeHandler();
                return Iterators.concat(ItemStackHandlerContainers.iterateNonEmpty(inventory), ItemStackHandlerContainers.iterateNonEmpty(upgrades));
            }
        }

    }
}
