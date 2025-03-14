package com.wanderersoftherift.wotr.block.blockentity;

import com.wanderersoftherift.wotr.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class RiftChestBlockEntity extends BaseContainerBlockEntity implements LidBlockEntity {
    public static final int SIZE = 27;
    private NonNullList<ItemStack> items = NonNullList.withSize(SIZE, ItemStack.EMPTY);

    public RiftChestBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.RIFT_CHEST.get(), pos, blockState);
    }

    @Override
    public int getContainerSize() {
        return SIZE;
    }

    @Override
    protected @NotNull NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    protected void setItems(@NotNull NonNullList<ItemStack> items) {
        this.items = items;
    }

    @Override
    protected @NotNull Component getDefaultName() {
        return Component.translatable("container.wotr.rift_chest");
    }

    @Override
    public @NotNull AbstractContainerMenu createMenu(int containerId, @NotNull Inventory inventory) {
        return new ChestMenu(MenuType.GENERIC_9x3, containerId, inventory, this, 3);
    }

    @Override
    public float getOpenNess(float v) {
        return v;
    }
}