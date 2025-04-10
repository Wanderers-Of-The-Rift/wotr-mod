package com.wanderersoftherift.wotr.mixin;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractContainerMenu.class)
public interface InvokerAbstractContainerMenu {
    @Invoker("dropOrPlaceInInventory")
    static void dropOrPlaceInInventory(Player player, ItemStack stack) {
    }

}
