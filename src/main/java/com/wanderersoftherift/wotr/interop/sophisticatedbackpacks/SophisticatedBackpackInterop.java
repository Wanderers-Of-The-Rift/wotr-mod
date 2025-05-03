package com.wanderersoftherift.wotr.interop.sophisticatedbackpacks;

import com.google.common.collect.Iterators;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.inventory.containers.ContainerItemWrapper;
import com.wanderersoftherift.wotr.core.inventory.containers.ContainerType;
import com.wanderersoftherift.wotr.core.inventory.containers.ContainerWrapper;
import com.wanderersoftherift.wotr.core.inventory.containers.ItemStackHandlerContainers;
import com.wanderersoftherift.wotr.init.ModContainerTypes;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.RegisterEvent;
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

    }

    public static void register(RegisterEvent event) {
        event.register(ModContainerTypes.CONTAINER_TYPE_KEY, registry -> registry
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
                return Iterators.concat(ItemStackHandlerContainers.iterateNonEmpty(inventory),
                        ItemStackHandlerContainers.iterateNonEmpty(upgrades));
            }
        }

    }
}
