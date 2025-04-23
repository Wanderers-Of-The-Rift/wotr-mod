package com.wanderersoftherift.wotr.block.blockentity;

import com.google.common.base.Preconditions;
import com.wanderersoftherift.wotr.gui.menu.RuneAnvilContainer;
import com.wanderersoftherift.wotr.gui.menu.RuneAnvilMenu;
import com.wanderersoftherift.wotr.init.ModBlockEntities;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

@MethodsReturnNonnullByDefault
public class RuneAnvilBlockEntity extends BaseContainerBlockEntity implements RuneAnvilContainer {
    private static final Component CONTAINER_TITLE = Component.translatable("container.wotr.rune_anvil");
    private NonNullList<ItemStack> items = NonNullList.withSize(SIZE, ItemStack.EMPTY);

    public RuneAnvilBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.RUNE_ANVIL_BLOCK_ENTITY.get(), pos, blockState);
    }

    @Override
    protected Component getDefaultName() {
        return CONTAINER_TITLE;
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    protected void setItems(@NotNull NonNullList<ItemStack> nonNullList) {
        this.items = nonNullList;
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, @NotNull Inventory inventory) {
        Preconditions.checkState(this.level != null, "Attempted to create a menu for a block entity without a level");

        return new RuneAnvilMenu(containerId, inventory, ContainerLevelAccess.create(this.level, this.worldPosition),
                true, this);
    }
}
