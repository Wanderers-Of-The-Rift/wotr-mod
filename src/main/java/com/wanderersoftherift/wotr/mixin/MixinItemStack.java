package com.wanderersoftherift.wotr.mixin;

import com.wanderersoftherift.wotr.item.socket.GearSockets;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public class MixinItemStack {

    @Inject(method = "onCraftedBy", at = @At("TAIL"))
    public void onCraftedByGenerateDataComponents(Level level, Player player, int amount, CallbackInfo ci) {
        ItemStack stack = (ItemStack) (Object) this;
        GearSockets.generateForItem(stack, level, player);
    }
    @Inject(method = "onCraftedBySystem", at = @At("TAIL"))
    public void onCraftedBySystemGenerateDataComponents(Level level, CallbackInfo ci) {
        ItemStack stack = (ItemStack) (Object) this;
        GearSockets.generateForItem(stack, level, null);
    }
}
