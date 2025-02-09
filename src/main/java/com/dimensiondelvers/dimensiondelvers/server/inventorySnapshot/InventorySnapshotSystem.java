package com.dimensiondelvers.dimensiondelvers.server.inventorySnapshot;

import com.dimensiondelvers.dimensiondelvers.init.ModAttachments;
import com.dimensiondelvers.dimensiondelvers.init.ModDataComponentType;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;

import java.util.*;

/**
 * Implementation for capture, update and restoration of Inventory Snapshots
 * <p></p>
 * The envisioned behavior is:
 * <ul>
 *     <li>When a snapshot is initially captured, all items in the players inventory and sub-inventories are enumerated</li>
 *     <li>When a player dies, the player's inventory is compared to the snapshot. The snapshot is reduced to the remaining items and any excess is dropped (mechanism TBD)</li>
 *     <li>When a player respawns the items in the snapshot are returned and the snapshot is removed</li>
 * </ul>
 * <p></p>
 * The implementation is to tag any non-stackable items, and directly record any stackable, with the assumption that stackable items will
 * not vary in a non-comparable manner.
 */
public class InventorySnapshotSystem {

    private static final DataComponentPatch REMOVE_SNAPSHOT_ID_PATCH = DataComponentPatch.builder().remove(ModDataComponentType.INVENTORY_SNAPSHOT_ID.get()).build();

    /**
     * Generates a snapshot for the given player
     *
     * @param player
     */
    public static void captureSnapshot(ServerPlayer player) {
        clearItemIds(player);
        player.setData(ModAttachments.INVENTORY_SNAPSHOT, InventorySnapshot.capture(player));
    }

    /**
     * Clears any snapshot on the player
     *
     * @param player
     */
    public static void clearSnapshot(ServerPlayer player) {
        clearItemIds(player);
        player.setData(ModAttachments.INVENTORY_SNAPSHOT, new InventorySnapshot());
    }

    /**
     * Updates the snapshot for the player's death. This will reduce the captured items to what the player
     * still had on them at time of death.
     *
     * @param player
     * @param event
     */
    public static void updateSnapshotForDeath(ServerPlayer player, LivingDropsEvent event) {
        InventorySnapshot snapshot = player.getData(ModAttachments.INVENTORY_SNAPSHOT);
        if (snapshot.itemIds().isEmpty() && snapshot.items().isEmpty()) {
            return;
        }

        DeathDropCalculator refiner = new DeathDropCalculator(player, snapshot, event.getDrops());

        event.getDrops().clear();
        event.getDrops().addAll(refiner.dropItems);

        player.setData(ModAttachments.INVENTORY_SNAPSHOT, new InventorySnapshot(Collections.emptyList(), refiner.retainItems));
    }

    /**
     * Populate the player's inventory with all items from their snapshot, drop any that don't fit
     *
     * @param player
     */
    public static void restoreFromSnapshot(ServerPlayer player) {
        InventorySnapshot snapshot = player.getData(ModAttachments.INVENTORY_SNAPSHOT);

        for (ItemStack item : snapshot.items()) {
            if (!player.getInventory().add(item)) {
                item.applyComponents(REMOVE_SNAPSHOT_ID_PATCH);
                player.level().addFreshEntity(new ItemEntity(player.level(), player.position().x, player.position().y, player.position().z, item));
            }
        }
        player.setData(ModAttachments.INVENTORY_SNAPSHOT, new InventorySnapshot());
        clearItemIds(player);
    }

    private static final class DeathDropCalculator {
        private final List<ItemStack> retainItems = new ArrayList<>();
        private final List<ItemEntity> dropItems = new ArrayList<>();

        private ServerPlayer player;
        private List<ItemStack> snapshotItems;
        private Set<UUID> snapshotItemIds;

        public DeathDropCalculator(ServerPlayer player, InventorySnapshot snapshot, Collection<ItemEntity> heldItems) {
            this.player = player;
            this.snapshotItems = new ArrayList<>(snapshot.items());
            this.snapshotItemIds = snapshot.itemIds();
            processInventoryItems(heldItems);
        }

        private void processInventoryItems(Collection<ItemEntity> drops) {
            for (ItemEntity itemEntity : drops) {
                ItemStack item = itemEntity.getItem();

                if (item.getComponents().has(ModDataComponentType.INVENTORY_SNAPSHOT_ID.get()) && snapshotItemIds.contains(item.getComponents().get(ModDataComponentType.INVENTORY_SNAPSHOT_ID.get()))) {
                    if (item.has(DataComponents.CONTAINER)) {
                        processContainerContents(item, true);
                    }
                    retainItems.add(item);
                } else if (item.isStackable()) {
                    int dropCount = calculateDropCount(item);

                    if (dropCount < item.getCount()) {
                        retainItems.add(item.split(item.getCount() - dropCount));
                    }
                    if (!item.isEmpty()) {
                        dropItems.add(itemEntity);
                    }
                } else {
                    if (item.has(DataComponents.CONTAINER)) {
                        processContainerContents(item, false);
                    }
                    itemEntity.getItem().applyComponents(REMOVE_SNAPSHOT_ID_PATCH);
                    dropItems.add(itemEntity);
                }
            }
        }

        // If we're retaining the container
        // - Any item that should be retained keep in container
        // - Any item that we don't want, copy and clear and drop the copy
        // If we're not retaining the container
        // - Any item that should be retained copy and clear and put the copy in retain
        // - Any item that we don't want, keep in container
        private void processContainerContents(ItemStack containerItem, boolean retainingContainer) {
            ItemContainerContents itemContainerContents = containerItem.get(DataComponents.CONTAINER);
            for (ItemStack item : itemContainerContents.nonEmptyItems()) {
                if (item.getComponents().has(ModDataComponentType.INVENTORY_SNAPSHOT_ID.get()) && snapshotItemIds.contains(item.getComponents().get(ModDataComponentType.INVENTORY_SNAPSHOT_ID.get()))) {
                    if (item.has(DataComponents.CONTAINER)) {
                        processContainerContents(item, true);
                    }

                    if (!retainingContainer) {
                        retainItems.add(item.copyAndClear());
                    }
                } else if (item.isStackable()) {
                    int dropCount = calculateDropCount(item);

                    if (retainingContainer && dropCount > 0) {
                        dropItems.add(createItemEntity(item.split(dropCount)));
                    } else if (!retainingContainer && dropCount < item.getCount()) {
                        retainItems.add(item.split(item.getCount() - dropCount));
                    }
                } else if (retainingContainer) {
                    item.applyComponents(REMOVE_SNAPSHOT_ID_PATCH);
                    dropItems.add(createItemEntity(item.copyAndClear()));
                }
            }
        }

        private int calculateDropCount(ItemStack item) {
            int dropCount = item.getCount();

            // Walk through the list of snapshotted items, reducing stack counts of matching stacks until all items are accounted for
            int index = 0;
            while (dropCount > 0 && index < snapshotItems.size()) {
                ItemStack snapshotItem = snapshotItems.get(index);
                if (ItemStack.isSameItemSameComponents(item, snapshotItem)) {
                    if (dropCount <= snapshotItem.getCount()) {
                        snapshotItem.shrink(dropCount);
                        dropCount = 0;
                    } else {
                        snapshotItems.remove(index);
                        dropCount -= snapshotItem.getCount();
                    }
                } else {
                    index++;
                }
            }
            return dropCount;
        }

        private ItemEntity createItemEntity(ItemStack stack) {
            return new ItemEntity(player.level(), player.position().x, player.position().y, player.position().z, stack);
        }
    }

    /**
     * Clears all snapshot item id components from items in the player's inventory
     *
     * @param player
     */
    private static void clearItemIds(ServerPlayer player) {
        DataComponentPatch removeIdPatch = DataComponentPatch.builder().remove(ModDataComponentType.INVENTORY_SNAPSHOT_ID.get()).build();
        for (ItemStack item : player.getInventory().items) {
            item.applyComponents(removeIdPatch);
        }
        for (ItemStack item : player.getInventory().armor) {
            item.applyComponents(removeIdPatch);
        }
        for (ItemStack item : player.getInventory().offhand) {
            item.applyComponents(removeIdPatch);
        }
    }
}
