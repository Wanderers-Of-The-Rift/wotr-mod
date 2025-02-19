package com.dimensiondelvers.dimensiondelvers.block.blockentity;

import com.dimensiondelvers.dimensiondelvers.gui.menu.RuneAnvilMenu;
import com.dimensiondelvers.dimensiondelvers.init.ModBlockEntities;
import com.dimensiondelvers.dimensiondelvers.init.ModDataComponentType;
import com.dimensiondelvers.dimensiondelvers.init.ModItems;
import com.dimensiondelvers.dimensiondelvers.item.runegem.RunegemData;
import com.dimensiondelvers.dimensiondelvers.item.socket.GearSocket;
import com.dimensiondelvers.dimensiondelvers.item.socket.GearSockets;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RuneAnvilBlockEntity extends BaseContainerBlockEntity {
    public static final int SIZE = 7;
    private static final Component CONTAINER_TITLE = Component.translatable("container.dimensiondelvers.rune_anvil");
    private NonNullList<ItemStack> items = NonNullList.withSize(SIZE, ItemStack.EMPTY);
    private ContainerListener listener;

    public RuneAnvilBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.RUNE_ANVIL_BLOCK_ENTITY.get(), pos, blockState);
    }

    @Override
    protected @NotNull Component getDefaultName() {
        return CONTAINER_TITLE;
    }

    @Override
    protected @NotNull NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    protected void setItems(@NotNull NonNullList<ItemStack> nonNullList) {
        this.items = nonNullList;
    }

    @Override
    protected @NotNull AbstractContainerMenu createMenu(int containerId, @NotNull Inventory inventory) {
        if (this.level == null) {
            throw new IllegalStateException("Attempted to create a menu for a block entity without a level");
        }

        return new RuneAnvilMenu(containerId, inventory, ContainerLevelAccess.create(this.level, this.worldPosition), true, this);
    }

    @Override
    public int getContainerSize() {
        return SIZE;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    private ItemStack gear() {
        return this.items.getFirst();
    }

    @Override
    public boolean canPlaceItem(int slot, @NotNull ItemStack stack) {
        if (slot == 0) {
            return stack.has(ModDataComponentType.GEAR_SOCKETS);
        } else {
            int runegemSlotIndex = slot - 1;

            if (!stack.is(ModItems.RUNEGEM)) return false;

            ItemStack gear = this.gear();
            GearSockets gearSockets = gear.get(ModDataComponentType.GEAR_SOCKETS.get());
            RunegemData runegemData = stack.get(ModDataComponentType.RUNEGEM_DATA.get());
            if (gear.isEmpty() || stack.isEmpty() || gearSockets == null || runegemData == null) return false;

            List<GearSocket> sockets = gearSockets.sockets();
            if (sockets.size() <= runegemSlotIndex) return false;

            GearSocket socket = sockets.get(runegemSlotIndex);
            return socket.canBeApplied(runegemData);
        }
    }

    @Override
    public boolean canTakeItem(@NotNull Container target, int slot, @NotNull ItemStack stack) {
        if (slot == 0) {
            return true;
        } else {
            int runegemSlotIndex = slot - 1;

            ItemStack gear = this.gear();
            GearSockets gearSockets = gear.get(ModDataComponentType.GEAR_SOCKETS.get());
            RunegemData runegemData = stack.get(ModDataComponentType.RUNEGEM_DATA.get());
            if (gear.isEmpty() || stack.isEmpty() || gearSockets == null || runegemData == null)
                return true; // i'm not sure this is right

            List<GearSocket> sockets = gearSockets.sockets();
            if (sockets.size() <= runegemSlotIndex) return false;

            GearSocket socket = sockets.get(runegemSlotIndex);
            RunegemData appliedRunegem = socket.runegem().orElse(null);
            if (appliedRunegem == null) return true;
            return !appliedRunegem.equals(runegemData);
        }
    }
}
