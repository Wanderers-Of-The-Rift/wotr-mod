package com.wanderersoftherift.wotr.gui.menu.slot;

import net.minecraft.core.UUIDUtil;
import net.minecraft.world.inventory.DataSlot;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.IntStream;

/**
 * Composite DataSlot for holding and replicating a UUID
 */
public class UUIDDataSlot {
    private final int[] data = new int[4];

    public void set(UUID uuid) {
        int[] value = UUIDUtil.uuidToIntArray(uuid);
        System.arraycopy(value, 0, data, 0, data.length);
    }

    public UUID get() {
        return UUIDUtil.uuidFromIntArray(data);
    }

    public Collection<DataSlot> createSlots() {
        return IntStream.range(0, data.length).mapToObj(i -> DataSlot.shared(data, i)).toList();
    }
}
