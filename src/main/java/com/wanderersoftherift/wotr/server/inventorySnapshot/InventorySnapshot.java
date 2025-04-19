package com.wanderersoftherift.wotr.server.inventorySnapshot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * InventorySnapshot is used to record the contents of a player's inventory at a point in time.
 * <p>
 * This is composed of an id which all non-stackable items will be marked with and ItemStacks corresponding to all
 * stackable items
 * </p>
 */
public class InventorySnapshot {

    private final UUID id;
    private final List<ItemStack> items;

    public static final Codec<ItemStack> NONSTRICT_ITEMSTACK_CODEC = Codec.lazyInitialized(() -> RecordCodecBuilder
            .create(instance -> instance.group(Item.CODEC.fieldOf("id").forGetter(ItemStack::getItemHolder),
                    ExtraCodecs.POSITIVE_INT.fieldOf("count").forGetter(ItemStack::getCount),
                    DataComponentPatch.CODEC.optionalFieldOf("components", DataComponentPatch.EMPTY).forGetter(x -> {
                        if (x.getComponents() instanceof PatchedDataComponentMap patchedMap) {
                            return patchedMap.asPatch();
                        }
                        return DataComponentPatch.EMPTY;
                    })).apply(instance, ItemStack::new)));

    public static final Codec<InventorySnapshot> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(UUIDUtil.CODEC.fieldOf("snapshotId").forGetter(x -> x.id),
                    NONSTRICT_ITEMSTACK_CODEC.listOf().fieldOf("items").forGetter(x -> x.items))
            .apply(instance, InventorySnapshot::new));

    public InventorySnapshot() {
        this(new UUID(0, 0), Collections.emptyList());
    }

    public InventorySnapshot(UUID id, Collection<ItemStack> items) {
        this.id = id;
        this.items = new ArrayList<>(items);
    }

    public boolean isEmpty() {
        return id.getMostSignificantBits() == 0 && id.getLeastSignificantBits() == 0;
    }

    public UUID id() {
        return id;
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
            return Objects.equals(id, other.id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
