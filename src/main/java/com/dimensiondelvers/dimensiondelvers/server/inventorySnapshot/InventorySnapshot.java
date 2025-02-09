package com.dimensiondelvers.dimensiondelvers.server.inventorySnapshot;

import com.dimensiondelvers.dimensiondelvers.init.ModDataComponentType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;

import java.util.*;

/**
 * InventorySnapshot is used to record the contents of a player's inventory at a point in time.
 */
public class InventorySnapshot {
    private final Set<UUID> itemIds;
    private final List<ItemStack> items;

    public static final Codec<InventorySnapshot> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                        UUIDUtil.CODEC.listOf().fieldOf("itemIds").forGetter(x -> new ArrayList<>(x.itemIds)),
                        ItemStack.CODEC.listOf().fieldOf("items").forGetter(x -> x.items)
                    ).apply(instance, InventorySnapshot::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, InventorySnapshot> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC.apply(ByteBufCodecs.list()),
            x -> new ArrayList<>(x.itemIds),
            ItemStack.STREAM_CODEC.apply(ByteBufCodecs.list()),
            InventorySnapshot::items,
            InventorySnapshot::new
    );

    /**
     * Generates an InventorySnapshot for a player's inventory
     * @param player
     * @return A new InventorySnapshot
     */
    public static InventorySnapshot capture(ServerPlayer player) {
        Set<UUID> itemIds = new LinkedHashSet<>();
        List<ItemStack> itemStacks = new ArrayList<>();

        for (ItemStack item : player.getInventory().items) {
            captureItem(item, itemIds, itemStacks);
        }
        for (ItemStack item : player.getInventory().armor) {
            captureItem(item, itemIds, itemStacks);
        }
        captureItem(player.getOffhandItem(), itemIds, itemStacks);
        return new InventorySnapshot(itemIds, itemStacks);
    }

    private static void captureItem(ItemStack item, Set<UUID> itemIds, List<ItemStack> itemStacks) {
        if (item.isEmpty()) {
            return;
        }
        if (item.isStackable()) {
            itemStacks.add(item.copy());
        } else {
            UUID id = UUID.randomUUID();
            item.applyComponents(DataComponentPatch.builder().set(ModDataComponentType.INVENTORY_SNAPSHOT_ID.get(), id).build());
            itemIds.add(id);

            if (item.has(DataComponents.CONTAINER)) {
                ItemContainerContents contents = item.get(DataComponents.CONTAINER);
                for (ItemStack nonEmptyItem : contents.nonEmptyItems()) {
                    captureItem(nonEmptyItem, itemIds, itemStacks);
                }
            }
        }
    }

    public InventorySnapshot() {
        this(Collections.emptyList(), Collections.emptyList());
    }

    public InventorySnapshot(Collection<UUID> itemIds, Collection<ItemStack> items) {
        this.itemIds = new LinkedHashSet<>(itemIds);
        this.items = new ArrayList<>(items);
    }

    public Set<UUID> itemIds() {
        return Collections.unmodifiableSet(itemIds);
    }

    public List<ItemStack> items() {
        return Collections.unmodifiableList(items);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof InventorySnapshot other) {
            return Objects.equals(itemIds, other.itemIds) && Objects.equals(items, other.items);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemIds, items);
    }
}
