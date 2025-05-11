package com.wanderersoftherift.wotr.mixin;

import com.wanderersoftherift.wotr.init.ModDataComponentType;
import com.wanderersoftherift.wotr.item.implicit.GearImplicits;
import com.wanderersoftherift.wotr.item.socket.GearSockets;
import net.minecraft.server.level.ServerLevel;
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
        if (level.isClientSide()) {
            return;
        }
        ServerLevel serverLevel = (ServerLevel) level;
        GearSockets.generateForItem(stack, level, 1, 1);
        GearImplicits implicits = stack.get(ModDataComponentType.GEAR_IMPLICITS);
        if (implicits != null) {
            implicits.modifierInstances(stack, serverLevel);
        }
    }
}