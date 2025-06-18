package com.wanderersoftherift.wotr.gui.menu;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class QuickMover {
    public static final int PLAYER_INVENTORY_SLOTS = 3 * 9;
    public static final int PLAYER_SLOTS = PLAYER_INVENTORY_SLOTS + 9;

    private final AbstractContainerMenu menu;
    private final List<SlotMover> slotMovers;

    private QuickMover(AbstractContainerMenu menu, List<SlotMover> slotMovers) {
        this.slotMovers = ImmutableList.copyOf(slotMovers);
        this.menu = menu;
    }

    public ItemStack quickMove(Player player, int slotIndex) {
        Slot slot = menu.slots.get(slotIndex);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack slotStack = slot.getItem().copy();
        ItemStack resultStack = slotStack.copy();
        return slotMovers.stream().filter(x -> x.isFor(slotIndex)).findFirst().map(mover -> {
            MoveResult result = MoveResult.NO_MOVE;
            for (MoveAction moveAction : mover.moveActions) {
                if (moveAction.onlyIfNoOtherValid && result != MoveResult.NO_MOVE) {
                    break;
                }
                MoveResult newResult = moveItemStackTo(slotStack, moveAction.startSlot,
                        moveAction.startSlot + moveAction.count, moveAction.reverse);
                if (newResult == MoveResult.MOVED
                        || (newResult == MoveResult.VALID_BUT_FULL && result != MoveResult.MOVED)) {
                    result = newResult;
                }
                if (slotStack.isEmpty()) {
                    break;
                }
            }
            if (result == MoveResult.MOVED) {
                slot.onQuickCraft(slotStack, resultStack);
                if (slotStack.isEmpty()) {
                    slot.set(ItemStack.EMPTY);
                } else {
                    slot.set(slotStack);
                }
                return resultStack;
            }
            return ItemStack.EMPTY;
        }).orElse(ItemStack.EMPTY);
    }

    /**
     * Custom moveItemStackTo that is more compatible with ItemHandlers (by not modifying retrieved item stacks
     * 
     * @param stack            The stack to try and insert
     * @param startIndex       The lower slot index
     * @param endIndex         The higher slot index (exclusive)
     * @param reverseDirection Whether to reverse the slot check direction
     * @return The result of the move
     */
    private MoveResult moveItemStackTo(
            @NotNull ItemStack stack,
            int startIndex,
            int endIndex,
            boolean reverseDirection) {
        MoveResult result = MoveResult.NO_MOVE;
        int i, dir;
        if (reverseDirection) {
            i = endIndex - 1;
            dir = -1;
        } else {
            i = startIndex;
            dir = 1;
        }

        if (stack.isStackable()) {
            while (!stack.isEmpty() && (reverseDirection ? i >= startIndex : i < endIndex)) {
                Slot slot = menu.slots.get(i);
                ItemStack existingStack = slot.getItem().copy();
                if (!existingStack.isEmpty() && ItemStack.isSameItemSameComponents(stack, existingStack)) {
                    int j = existingStack.getCount() + stack.getCount();
                    int k = slot.getMaxStackSize(existingStack);
                    if (j <= k) {
                        stack.setCount(0);
                        existingStack.setCount(j);
                        slot.set(existingStack);
                        result = MoveResult.MOVED;
                    } else if (existingStack.getCount() < k) {
                        stack.shrink(k - existingStack.getCount());
                        existingStack.setCount(k);
                        slot.set(existingStack);
                        result = MoveResult.MOVED;
                    } else if (result != MoveResult.MOVED) {
                        result = MoveResult.VALID_BUT_FULL;
                    }
                }

                i += dir;
            }
        }

        if (!stack.isEmpty()) {
            if (reverseDirection) {
                i = endIndex - 1;
            } else {
                i = startIndex;
            }

            while (reverseDirection ? i >= startIndex : i < endIndex) {
                Slot targetSlot = menu.slots.get(i);
                ItemStack existingItem = targetSlot.getItem();
                if (targetSlot.mayPlace(stack)) {
                    if (existingItem.isEmpty()) {
                        int l = targetSlot.getMaxStackSize(stack);
                        ItemStack split = stack.split(Math.min(stack.getCount(), l));
                        targetSlot.setByPlayer(split);
                        result = MoveResult.MOVED;
                        break;
                    } else if (result != MoveResult.MOVED) {
                        result = MoveResult.VALID_BUT_FULL;
                    }
                }

                i += dir;
            }
        }

        return result;
    }

    public static QuickMover.Builder create(AbstractContainerMenu menu) {
        return new QuickMover.Builder(menu);
    }

    private static class SlotMover {
        private final int start;
        private final int count;
        private final List<MoveAction> moveActions;

        private SlotMover(int start, int count, List<MoveAction> moveActions) {
            this.start = start;
            this.count = count;
            this.moveActions = moveActions;
        }

        public boolean isFor(int slot) {
            return slot >= start && slot < start + count;
        }
    }

    public static class Builder {
        private static final int UNSET = -1;

        private int playerSlotsStart = UNSET;
        private AbstractContainerMenu menu;

        private List<SlotMover> slotMovers = new ArrayList<>();

        private Builder(AbstractContainerMenu menu) {
            this.menu = menu;
        }

        /**
         * Set the start index for the player slots (inventory + hotbar)
         *
         * @param start
         * @return
         */
        public SlotMoverBuilder forPlayerSlots(int start) {
            Preconditions.checkState(playerSlotsStart == UNSET, "Player slots already specified.");
            playerSlotsStart = start;
            return new PlayerSlotMoverBuilder(start);
        }

        /**
         * Start setup for the given slot
         *
         * @param slot
         * @return
         */
        public SlotMoverBuilder forSlot(int slot) {
            return new SlotMoverBuilder(slot, 1);
        }

        /**
         * Start setup for the given slot range
         *
         * @param startSlot
         * @param count
         * @return
         */
        public SlotMoverBuilder forSlots(int startSlot, int count) {
            return new SlotMoverBuilder(startSlot, count);
        }

        /**
         * @return The finalised QuickMover
         */
        public QuickMover build() {
            return new QuickMover(menu, slotMovers);
        }

        public class SlotMoverBuilder {
            protected final int start;
            protected final int count;
            protected List<MoveAction> moveActions = new ArrayList<>();

            private SlotMoverBuilder(int start, int count) {
                this.start = start;
                this.count = count;
            }

            /**
             * Sets the current slots to try to move to the specified slot
             *
             * @param slot
             * @return
             */
            public SlotMoverBuilder tryMoveTo(int slot) {
                return tryMoveTo(slot, 1, false);
            }

            /**
             * Sets the current slots to try to move to the specified slots
             *
             * @param startSlot
             * @param count
             * @return
             */
            public SlotMoverBuilder tryMoveTo(int startSlot, int count) {
                return tryMoveTo(startSlot, count, false);
            }

            /**
             * Sets the current slots to try to move to the specified slots
             *
             * @param startSlot
             * @param count
             * @param reverse
             * @return
             */
            public SlotMoverBuilder tryMoveTo(int startSlot, int count, boolean reverse) {
                moveActions.add(new MoveAction(startSlot, count, reverse, false));
                return this;
            }

            /**
             *
             * @return
             */
            public SlotMoverBuilder tryMoveToPlayer() {
                Preconditions.checkState(playerSlotsStart != UNSET, "Player slots must be specified first.");
                return tryMoveTo(playerSlotsStart, PLAYER_SLOTS, true);
            }

            /**
             * Set the start index for the player slots (inventory + hotbar)
             *
             * @param start
             * @return
             */
            public SlotMoverBuilder forPlayerSlots(int start) {
                createSlotMovers();
                return Builder.this.forPlayerSlots(start);
            }

            /**
             * Start setup for the given slot
             *
             * @param slot
             * @return
             */
            public SlotMoverBuilder forSlot(int slot) {
                createSlotMovers();
                return Builder.this.forSlot(slot);
            }

            /**
             * Start setup for the given slot range
             *
             * @param startSlot
             * @param count
             * @return
             */
            public SlotMoverBuilder forSlots(int startSlot, int count) {
                createSlotMovers();
                return Builder.this.forSlots(startSlot, count);
            }

            /**
             * @return The finalised QuickMover
             */
            public QuickMover build() {
                createSlotMovers();
                return Builder.this.build();
            }

            protected void createSlotMovers() {
                slotMovers.add(new SlotMover(start, count, ImmutableList.copyOf(moveActions)));
            }
        }

        private class PlayerSlotMoverBuilder extends SlotMoverBuilder {

            private PlayerSlotMoverBuilder(int start) {
                super(start, PLAYER_SLOTS);
            }

            @Override
            protected void createSlotMovers() {
                // Main inventory
                List<MoveAction> mainActions = new ArrayList<>(moveActions);
                mainActions.add(new MoveAction(playerSlotsStart + PLAYER_INVENTORY_SLOTS, 9, false, true));
                slotMovers.add(new SlotMover(start, PLAYER_INVENTORY_SLOTS, ImmutableList.copyOf(mainActions)));
                // Hotbar inventory
                List<MoveAction> hotbarActions = new ArrayList<>(moveActions);
                hotbarActions.add(new MoveAction(playerSlotsStart, PLAYER_INVENTORY_SLOTS, false, true));
                slotMovers.add(new SlotMover(start + PLAYER_INVENTORY_SLOTS, 9, ImmutableList.copyOf(hotbarActions)));
            }

        }
    }

    /**
     * @param startSlot          The start slot to consider for the move action
     * @param count              How many slots to consider for the move action
     * @param reverse            Should the slots be considered in reverse
     * @param onlyIfNoOtherValid Should this action be skipped if there were other valid (but potentially full) moves in
     *                           previous MoveActions.
     */
    private record MoveAction(int startSlot, int count, boolean reverse, boolean onlyIfNoOtherValid) {
    }

    private enum MoveResult {
        // An item was moved
        MOVED,
        // No item moved, but it would have been valid if a slot was not full. This prevents partial moves followed by
        // inventory/hotbar swaps
        VALID_BUT_FULL,
        // No move
        NO_MOVE
    }

}