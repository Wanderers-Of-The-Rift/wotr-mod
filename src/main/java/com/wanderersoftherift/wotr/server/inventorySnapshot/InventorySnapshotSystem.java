package com.wanderersoftherift.wotr.server.inventorySnapshot;

import com.wanderersoftherift.wotr.init.ModAttachments;
import com.wanderersoftherift.wotr.init.ModDataComponentType;
import com.wanderersoftherift.wotr.server.inventorySnapshot.containers.*;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * System for capturing Inventory Snapshots and applying them on death and respawn of a player
 * <p>
 * The envisioned behavior is:
 * </p>
 * <ul>
 *     <li>When a snapshot is initially captured, all items in the players inventory and sub-inventories are enumerated</li>
 *     <li>When a player dies, the player's inventory is compared to the snapshot. Their inventory is split into a set of items that they have from the snapshot,
 *     and a set of items that are new</li>
 *     <li>When a player respawns the items they still had from the snapshot are returned and the snapshot is removed</li>
 *     <li>All other items are dropped where they died</li>
 * </ul>
 * <p>
 * Snapshots are created by adding a unique snapshot id to any non-stackable items, and directly record any stackable, with the assumption that stackable items will
 * not vary in a non-comparable manner.
 * </p>
 */
public class InventorySnapshotSystem {

    private static final DataComponentPatch REMOVE_SNAPSHOT_ID_PATCH = DataComponentPatch.builder().remove(ModDataComponentType.INVENTORY_SNAPSHOT_ID.get()).build();

    private static final InventorySnapshotSystem instance = new InventorySnapshotSystem();

    private final List<ContainerType> containerStrategies = new ArrayList<>();

    public static InventorySnapshotSystem getInstance() {
        return instance;
    }

    private InventorySnapshotSystem() {
        containerStrategies.add(new ComponentContainerType());
        containerStrategies.add(new BundleContainerType());
    }

    public void registerContainerStrategy(ContainerType strategy) {
        containerStrategies.add(strategy);
    }

    /**
     * Generates a snapshot for the given player
     *
     * @param player
     */
    public void captureSnapshot(ServerPlayer player) {
        clearItemIds(player);
        player.setData(ModAttachments.INVENTORY_SNAPSHOT, new InventorySnapshotBuilder(player).build());
    }

    /**
     * Clears any snapshot on the player
     *
     * @param player
     */
    public void clearSnapshot(ServerPlayer player) {
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
    public void retainSnapshotItemsOnDeath(ServerPlayer player, LivingDropsEvent event) {
        InventorySnapshot snapshot = player.getData(ModAttachments.INVENTORY_SNAPSHOT);
        if (snapshot.isEmpty()) {
            return;
        }

        RespawnItemsCalculator refiner = new RespawnItemsCalculator(player, snapshot, event.getDrops());

        event.getDrops().clear();
        event.getDrops().addAll(refiner.dropItems);

        player.setData(ModAttachments.RESPAWN_ITEMS, refiner.retainItems);
    }

    /**
     * Populate the player's inventory with all items from their snapshot, drop any that don't fit
     *
     * @param player
     */
    public void restoreItemsOnRespawn(ServerPlayer player) {
        for (ItemStack item : player.getData(ModAttachments.RESPAWN_ITEMS)) {
            if (!player.getInventory().add(item)) {
                item.applyComponents(REMOVE_SNAPSHOT_ID_PATCH);
                player.level().addFreshEntity(new ItemEntity(player.level(), player.position().x, player.position().y, player.position().z, item));
            }
        }
        player.setData(ModAttachments.RESPAWN_ITEMS, new ArrayList<>());
        clearItemIds(player);
    }

    public final class InventorySnapshotBuilder {

        private UUID snapshotId = UUID.randomUUID();
        private List<ItemStack> items = new ArrayList<>();

        private DataComponentPatch addSnapshotIdPatch = DataComponentPatch.builder().set(ModDataComponentType.INVENTORY_SNAPSHOT_ID.get(), snapshotId).build();

        /**
         * Generates an InventorySnapshot for a player's inventory
         *
         * @param player
         * @return A new InventorySnapshot
         */
        public InventorySnapshotBuilder(ServerPlayer player) {
            for (ItemStack item : player.getInventory().items) {
                captureItem(new DirectContainerItemWrapper(item));
            }
            for (ItemStack item : player.getInventory().armor) {
                captureItem(new DirectContainerItemWrapper(item));
            }
            captureItem(new DirectContainerItemWrapper(player.getOffhandItem()));
        }

        public InventorySnapshot build() {
            return new InventorySnapshot(snapshotId, items);
        }

        private void captureItem(ContainerItemWrapper containerItem) {
            ItemStack item = containerItem.getReadOnlyItemStack();
            if (item.isEmpty()) {
                return;
            }
            if (item.isStackable()) {
                items.add(item.copy());
            } else {
                containerItem.applyComponents(addSnapshotIdPatch);

                for (ContainerItemWrapper content : getContents(item)) {
                    captureItem(content);
                }
            }
        }

    }

    private final class RespawnItemsCalculator {
        private final List<ItemStack> retainItems = new ArrayList<>();
        private final List<ItemEntity> dropItems = new ArrayList<>();

        private final ServerPlayer player;
        private final List<ItemStack> snapshotItems;
        private final UUID snapshotId;

        public RespawnItemsCalculator(ServerPlayer player, InventorySnapshot snapshot, Collection<ItemEntity> heldItems) {
            this.player = player;
            this.snapshotItems = new ArrayList<>(snapshot.items());
            this.snapshotId = snapshot.id();
            processInventoryItems(heldItems);
        }

        private void processInventoryItems(Collection<ItemEntity> drops) {
            for (ItemEntity itemEntity : drops) {
                ItemStack item = itemEntity.getItem();

                if (item.isStackable()) {
                    int dropCount = calculateDropCount(item);
                    if (dropCount < item.getCount()) {
                        retainItems.add(item.split(item.getCount() - dropCount));
                    }
                    if (!item.isEmpty()) {
                        dropItems.add(itemEntity);
                    }
                } else {
                    boolean retainItem = shouldRetainNonStackable(item);

                    ContainerWrapper contents = getContents(item);
                    for (ContainerItemWrapper content : contents) {
                        processContainerItem(content, retainItem);
                    }
                    contents.recordChanges();

                    if (retainItem) {
                        retainItems.add(item);
                    } else {
                        itemEntity.getItem().applyComponents(REMOVE_SNAPSHOT_ID_PATCH);
                        dropItems.add(itemEntity);
                    }
                }
            }
        }

        private boolean shouldRetainNonStackable(ItemStack item) {
            return item.getComponents().has(ModDataComponentType.INVENTORY_SNAPSHOT_ID.get()) && snapshotId.equals(item.getComponents().get(ModDataComponentType.INVENTORY_SNAPSHOT_ID.get()));
        }

        // If we're retaining the container
        // - Any item that should be retained keep in container
        // - Any item that we don't want, copy and clear and drop the copy
        // If we're not retaining the container
        // - Any item that should be retained copy and clear and put the copy in retain
        // - Any item that we don't want, keep in container
        private void processContainerItem(ContainerItemWrapper containerItem, boolean retainingContainer) {
            ItemStack item = containerItem.getReadOnlyItemStack();
            if (item.isStackable()) {
                int dropCount = calculateDropCount(item);

                if (retainingContainer && dropCount > 0) {
                    dropItems.addAll(createItemEntity(containerItem.split(dropCount)));
                } else if (!retainingContainer && dropCount < item.getCount()) {
                    retainItems.addAll(containerItem.split(item.getCount() - dropCount));
                }
            } else {
                boolean retainItem = shouldRetainNonStackable(item);

                ContainerWrapper contents = getContents(item);
                for (ContainerItemWrapper content : contents) {
                    processContainerItem(content, retainItem);
                }
                contents.recordChanges();

                if (retainingContainer && !retainItem) {
                    List<ItemStack> dropItems = containerItem.remove();
                    dropItems.forEach(x -> x.applyComponents(REMOVE_SNAPSHOT_ID_PATCH));
                    this.dropItems.addAll(createItemEntity(dropItems));
                } else if (!retainingContainer && retainItem) {
                    retainItems.addAll(containerItem.remove());
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

        private List<ItemEntity> createItemEntity(List<ItemStack> stacks) {
            List<ItemEntity> entities = new ArrayList<>();
            for (ItemStack stack : stacks) {
                entities.add(new ItemEntity(player.level(), player.position().x, player.position().y, player.position().z, stack));
            }
            return entities;
        }
    }

    /**
     * Clears all snapshot item id components from items in the player's inventory
     *
     * @param player
     */
    private void clearItemIds(ServerPlayer player) {
        for (ItemStack item : player.getInventory().items) {
            clearItemIds(new DirectContainerItemWrapper(item));
        }
        for (ItemStack item : player.getInventory().armor) {
            clearItemIds(new DirectContainerItemWrapper(item));
        }
        for (ItemStack item : player.getInventory().offhand) {
            clearItemIds(new DirectContainerItemWrapper(item));
        }
    }

    private void clearItemIds(ContainerItemWrapper item) {
        item.applyComponents(REMOVE_SNAPSHOT_ID_PATCH);
        ContainerWrapper contents = getContents(item.getReadOnlyItemStack());
        for (ContainerItemWrapper content : contents) {
            clearItemIds(content);
        }
        contents.recordChanges();
    }

    /**
     * @param itemStack An item stack (that may or may not be a container)
     * @return An iterable over the contents of the given itemStack, if any
     */
    private ContainerWrapper getContents(ItemStack itemStack) {
        for (ContainerType strategy : containerStrategies) {
            if (strategy.isContainer(itemStack)) {
                return strategy.getWrapper(itemStack);
            }
        }
        return NonContainerWrapper.INSTANCE;
    }

}
