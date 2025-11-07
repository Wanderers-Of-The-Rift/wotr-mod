package com.wanderersoftherift.wotr.core.guild.trading;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.core.npc.NpcIdentity;
import com.wanderersoftherift.wotr.init.WotrDataComponentType;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Tracks available trades per merchant on the player
 */
public class AvailableTrades {
    public static final Codec<AvailableTrades> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.unboundedMap(NpcIdentity.CODEC, StockInventory.CODEC)
                    .fieldOf("merchant_stock")
                    .forGetter(x -> x.merchantStock)
    ).apply(instance, AvailableTrades::new));

    public static final int MERCHANT_INVENTORY_SIZE = 30;

    private final Map<Holder<NpcIdentity>, StockInventory> merchantStock;

    public AvailableTrades() {
        merchantStock = new HashMap<>();
    }

    private AvailableTrades(Map<Holder<NpcIdentity>, StockInventory> stock) {
        this.merchantStock = new HashMap<>(stock);
    }

    public Optional<IItemHandlerModifiable> getExisting(Holder<NpcIdentity> merchantId) {
        return Optional.ofNullable(merchantStock.get(merchantId));
    }

    public IItemHandlerModifiable create(Holder<NpcIdentity> merchantId, List<ItemStack> items) {
        StockInventory stock = new StockInventory(items);
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
            int extractCount = Integer.min(amount, original.getMaxStackSize());
            if (!simulate) {
                int remainder = original.getCount() - extractCount;
                if (remainder > 0) {
                    stock.get(slot).setCount(remainder);
                } else {
                    stock.set(slot, ItemStack.EMPTY);
                }
            }
            original.setCount(extractCount);
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
