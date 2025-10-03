package com.wanderersoftherift.wotr.core.guild.trading;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.init.WotrDataComponentType;
import net.minecraft.core.NonNullList;
import net.minecraft.core.UUIDUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Tracks available trades per merchant on the player
 */
public class AvailableTrades {
    public static final Codec<AvailableTrades> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.unboundedMap(UUIDUtil.STRING_CODEC, StockInventory.CODEC)
                    .fieldOf("merchant_stock")
                    .forGetter(x -> x.merchantStock)
    ).apply(instance, AvailableTrades::new));

    public static final int MERCHANT_INVENTORY_SIZE = 30;

    private final Map<UUID, StockInventory> merchantStock;

    public AvailableTrades() {
        merchantStock = new HashMap<>();
    }

    private AvailableTrades(Map<UUID, StockInventory> stock) {
        this.merchantStock = new HashMap<>(stock);
    }

    public @Nullable IItemHandlerModifiable getExisting(UUID merchantId) {
        return merchantStock.get(merchantId);
    }

    public IItemHandlerModifiable generate(UUID merchantId, LootTable lootTable, LootParams params) {
        StockInventory stock = new StockInventory(lootTable.getRandomItems(params));
        merchantStock.put(merchantId, stock);
        return stock;
    }

    private static class StockInventory implements IItemHandlerModifiable {

        public static final Codec<StockInventory> CODEC = NonNullList.codecOf(ItemStack.OPTIONAL_CODEC)
                .xmap(StockInventory::new, x -> x.stock);

        private final NonNullList<ItemStack> stock = NonNullList.withSize(MERCHANT_INVENTORY_SIZE, ItemStack.EMPTY);

        public StockInventory(List<ItemStack> initialStock) {
            for (int i = 0; i < initialStock.size() && i < stock.size(); i++) {
                if (initialStock.get(i).has(WotrDataComponentType.PRICE)) {
                    stock.set(i, initialStock.get(i));
                }
            }
        }

        @Override
        public void setStackInSlot(int slot, @NotNull ItemStack stack) {
            Preconditions.checkArgument(slot >= 0 && slot < stock.size(), "Slot out of bounds");
            stock.set(slot, stack);
        }

        @Override
        public int getSlots() {
            return stock.size();
        }

        @Override
        public @NotNull ItemStack getStackInSlot(int slot) {
            Preconditions.checkArgument(slot >= 0 && slot < stock.size(), "Slot out of bounds");
            return stock.get(slot);
        }

        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            return stack;
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            Preconditions.checkArgument(slot >= 0 && slot < stock.size(), "Slot out of bounds");
            if (amount == 0 || stock.get(slot).isEmpty()) {
                return ItemStack.EMPTY;
            }
            var original = stock.get(slot).copy();
            var extracted = Integer.min(amount, original.getMaxStackSize());
            if (!simulate) {
                var newCount = original.getCount() - extracted;
                if (newCount > 0) {
                    var newStack = stock.get(slot);
                    newStack.setCount(newCount);
                } else {
                    stock.set(slot, ItemStack.EMPTY);
                }
            }
            original.setCount(extracted);
            original.remove(WotrDataComponentType.PRICE);
            return original;
        }

        @Override
        public int getSlotLimit(int slot) {
            return Item.ABSOLUTE_MAX_STACK_SIZE;
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            Preconditions.checkArgument(slot >= 0 && slot < stock.size(), "Slot out of bounds");
            return true;
        }
    }

}
