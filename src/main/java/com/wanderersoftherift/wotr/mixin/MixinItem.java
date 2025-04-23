package com.wanderersoftherift.wotr.mixin;

import com.wanderersoftherift.wotr.item.socket.GearSockets;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Item.class)
public abstract class MixinItem {

    @Inject(method = "onCraftedPostProcess", at = @At("TAIL"))
    public void addCustomDataOnCraftedPostProcess(ItemStack stack, Level level, CallbackInfo ci) {
        // Add custom data to the ItemStack when it is crafted
        if (!level.isClientSide) {
            GearSockets.generateForItem(stack, level);
        }
    }
}