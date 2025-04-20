package com.wanderersoftherift.wotr.mixin;

import com.wanderersoftherift.wotr.init.ModTags;
import com.wanderersoftherift.wotr.item.socket.GearSockets;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class MixinItemStack {

    @Inject(method = "isDamageableItem", at = @At("HEAD"), cancellable = true)
    private void preventDurability(CallbackInfoReturnable<Boolean> cir) {
        ItemStack stack = ((ItemStack) (Object) this);
        if (!stack.is(ModTags.Items.UNBREAKABLE_EXCLUSIONS)) {
            cir.setReturnValue(false); // We may want certain items to be excluded, which can be done here, currently
                                       // all items are targeted that do not have the UNBREAKABLE_EXCLUSIONS tag
        }
    }

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
