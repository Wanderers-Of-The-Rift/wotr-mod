package com.wanderersoftherift.wotr.core.inventory.snapshot;

import com.wanderersoftherift.wotr.core.inventory.containers.ContainerItemWrapper;
import com.wanderersoftherift.wotr.core.inventory.containers.ContainerType;
import com.wanderersoftherift.wotr.core.inventory.containers.ContainerWrapper;
import com.wanderersoftherift.wotr.core.inventory.containers.DirectContainerItemWrapper;
import com.wanderersoftherift.wotr.core.inventory.containers.NonContainerWrapper;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.init.WotrDataComponentType;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * System for capturing Inventory Snapshots and applying them on death and respawn of a player
 * <p>
 * The envisioned behavior is:
 * </p>
 * <ul>
 * <li>When a snapshot is initially captured, all items in the players inventory and sub-inventories are enumerated</li>
 * <li>When a player dies, the player's inventory is compared to the snapshot. Their inventory is split into a set of
 * items that they have from the snapshot, and a set of items that are new</li>
 * <li>When a player respawns the items they still had from the snapshot are returned and the snapshot is removed</li>
 * <li>All other items are dropped where they died</li>
 * </ul>
 * <p>
 * Snapshots are created by adding a unique snapshot id to any non-stackable items, and directly record any stackable,
 * with the assumption that stackable items will not vary in a non-comparable manner.
 * </p>
 */
public final class InventorySnapshotSystem {

    private static final DataComponentPatch REMOVE_SNAPSHOT_ID_PATCH = DataComponentPatch.builder()
            .remove(WotrDataComponentType.INVENTORY_SNAPSHOT_ID.get())
            .build();

    private InventorySnapshotSystem() {
    }

    public static HashSet<UUID> snapshotsToIdSet(Stream<InventorySnapshot> snapshotStream) {
        var snapshotIds = new HashSet<UUID>();
        snapshotStream.map(InventorySnapshot::id).forEach(snapshotIds::add);
        return snapshotIds;
    }

    /**
     * Generates a snapshot for the given player. Stackable items that are part of any snapshot in `validSnapshot` will
     * not be overwritten.
     *
     * @param player         The player to generate a snapshot for
     * @param validSnapshots lis to snapshots that will continue to be valid even after death and thus should not be
     *                       removed
     */

    public static InventorySnapshot captureSnapshot(ServerPlayer player, List<InventorySnapshot> validSnapshots) {
        clearItemIds(player, snapshotsToIdSet(validSnapshots.stream()));
        return new InventorySnapshotBuilder(player).build();
    }

    /**
     * Updates the snapshot for the player's death. This will reduce the captured items to what the player still had on
     * them at time of death. Non-stackable items are restored only from `snapshot`, stackable items are restored from
     * all snapshots.
     *
     * @param player
     * @param event
     * @param snapshot       the newest snapshot to be restored
     * @param otherSnapshots snapshots other than `snapshot` that will also be restored
     */
    public static void retainSnapshotItemsOnDeath(
            ServerPlayer player,
            LivingDropsEvent event,
            InventorySnapshot snapshot,
            List<InventorySnapshot> otherSnapshots) {
        if (snapshot.isEmpty()) {
            return;
        }

        RespawnItemsCalculator refiner = new RespawnItemsCalculator(player, snapshot, otherSnapshots, event.getDrops());

        event.getDrops().clear();
        event.getDrops().addAll(refiner.dropItems);

        player.setData(WotrAttachments.RESPAWN_ITEMS, refiner.retainItems);
    }

    /**
     * Populate the player's inventory with all items from their snapshot, drop any that don't fit
     *
     * @param player
     * @param keptIds snapshot ids that might still be used
     */
    public static void restoreItemsOnRespawn(ServerPlayer player, Set<UUID> keptIds) {
        for (ItemStack item : player.getData(WotrAttachments.RESPAWN_ITEMS)) {
            if (player.getInventory().add(item)) {
                continue;
            }
            item.applyComponents(REMOVE_SNAPSHOT_ID_PATCH);
            player.level()
                    .addFreshEntity(new ItemEntity(player.level(), player.position().x, player.position().y,
                            player.position().z, item));
        }
        player.setData(WotrAttachments.RESPAWN_ITEMS, new ArrayList<>());
        clearItemIds(player, keptIds);
    }

    private static final class InventorySnapshotBuilder {
        private final Registry<ContainerType> containerTypes;
        private final UUID snapshotId = UUID.randomUUID();
        private final List<ItemStack> items = new ArrayList<>();

        private final DataComponentPatch addSnapshotIdPatch = DataComponentPatch.builder()
                .set(WotrDataComponentType.INVENTORY_SNAPSHOT_ID.get(), snapshotId)
                .build();

        /**
         * Generates an InventorySnapshot for a player's inventory
         *
         * @param player
         * @return A new InventorySnapshot
         */
        public InventorySnapshotBuilder(ServerPlayer player) {
            containerTypes = player.level().registryAccess().lookupOrThrow(WotrRegistries.Keys.CONTAINER_TYPES);
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
                return;
            }
            if (!item.has(WotrDataComponentType.INVENTORY_SNAPSHOT_ID)) {
                containerItem.applyComponents(addSnapshotIdPatch);
            }

            for (ContainerItemWrapper content : getContents(containerTypes, item)) {
                captureItem(content);
            }
        }

    }

    private static final class RespawnItemsCalculator {
        private final Registry<ContainerType> containerTypes;

        private final List<ItemStack> retainItems = new ArrayList<>();
        private final List<ItemEntity> dropItems = new ArrayList<>();

        private final ServerPlayer player;
        private final List<ItemStack> snapshotItems;
        private final Set<UUID> snapshotIds;

        public RespawnItemsCalculator(ServerPlayer player, InventorySnapshot lastSnapshot,
                List<InventorySnapshot> otherSnapshots, Collection<ItemEntity> heldItems) {
            this.player = player;
            this.containerTypes = player.level().registryAccess().lookupOrThrow(WotrRegistries.Keys.CONTAINER_TYPES);
            this.snapshotItems = new ArrayList<>(lastSnapshot.items());
            this.snapshotIds = snapshotsToIdSet(otherSnapshots.stream());
            this.snapshotIds.add(lastSnapshot.id());
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

                    ContainerWrapper contents = getContents(containerTypes, item);
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
            return item.getComponents().has(WotrDataComponentType.INVENTORY_SNAPSHOT_ID.get()) && snapshotIds
                    .contains(item.getComponents().get(WotrDataComponentType.INVENTORY_SNAPSHOT_ID.get()));
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

                ContainerWrapper contents = getContents(containerTypes, item);
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
            // Walk through the list of snapshotted items, reducing stack counts of matching stacks until all items are
            // accounted for
            int index = 0;
            while (dropCount > 0 && index < snapshotItems.size()) {
                ItemStack snapshotItem = snapshotItems.get(index);
                if (!ItemStack.isSameItemSameComponents(item, snapshotItem)) {
                    index++;
                    continue;
                }
                if (dropCount < snapshotItem.getCount()) {
                    var newStack = snapshotItem.copy();
                    newStack.shrink(dropCount);
                    snapshotItems.set(index, newStack);
                    return 0;
                }
                snapshotItems.remove(index);
                dropCount -= snapshotItem.getCount();
            }
            return dropCount;
        }

        private List<ItemEntity> createItemEntity(List<ItemStack> stacks) {
            List<ItemEntity> entities = new ArrayList<>();
            for (ItemStack stack : stacks) {
                entities.add(new ItemEntity(player.level(), player.position().x, player.position().y,
                        player.position().z, stack));
            }
            return entities;
        }
    }

    /**
     * Clears all snapshot item id components from items in the player's inventory
     *
     * @param player
     */
    private static void clearItemIds(ServerPlayer player, Set<UUID> keepIds) {
        Registry<ContainerType> containerTypes = player.getServer()
                .registryAccess()
                .lookupOrThrow(WotrRegistries.Keys.CONTAINER_TYPES);
        for (ItemStack item : player.getInventory().items) {
            clearItemIds(containerTypes, new DirectContainerItemWrapper(item), keepIds);
        }
        for (ItemStack item : player.getInventory().armor) {
            clearItemIds(containerTypes, new DirectContainerItemWrapper(item), keepIds);
        }
        for (ItemStack item : player.getInventory().offhand) {
            clearItemIds(containerTypes, new DirectContainerItemWrapper(item), keepIds);
        }
    }

    private static void clearItemIds(
            Registry<ContainerType> containerTypes,
            ContainerItemWrapper item,
            Set<UUID> keepIds) {
        var component = item.getReadOnlyItemStack().get(WotrDataComponentType.INVENTORY_SNAPSHOT_ID);
        if (component != null && !keepIds.contains(component)) {
            item.applyComponents(REMOVE_SNAPSHOT_ID_PATCH);
        }
        ContainerWrapper contents = getContents(containerTypes, item.getReadOnlyItemStack());
        for (ContainerItemWrapper content : contents) {
            clearItemIds(containerTypes, content, keepIds);
        }
        contents.recordChanges();
    }

    /**
     * @param itemStack An item stack (that may or may not be a container)
     * @return An iterable over the contents of the given itemStack, if any
     */
    private static ContainerWrapper getContents(Registry<ContainerType> containerTypes, ItemStack itemStack) {
        return containerTypes.stream()
                .filter(type -> type.isContainer(itemStack))
                .findAny()
                .map(strategy -> strategy.getWrapper(itemStack))
                .orElse(NonContainerWrapper.INSTANCE);
    }

}
