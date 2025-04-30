package com.wanderersoftherift.wotr.mixin;

import com.wanderersoftherift.wotr.init.ModTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.SetItemDamageFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SetItemDamageFunction.class)
public class MixinSetItemDamageFunction {

    // Prevents the log from being spammed whenever this runs
    @Inject(method = "run", at = @At("HEAD"), cancellable = true)
    private void preventConsoleSpam(ItemStack stack, LootContext context, CallbackInfoReturnable<ItemStack> cir) {
        if (!stack.is(ModTags.Items.UNBREAKABLE_EXCLUSIONS)) {
            cir.setReturnValue(stack);
        }
    }
}
