package com.wanderersoftherift.wotr.mixin;

import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ToolMaterial.class)
public class MixinToolMaterial {

    @Inject(method = "createSwordAttributes", at = @At("HEAD"), cancellable = true)
    private void deleteDefaultSwordProperties(
            float attackDamage,
            float attackSpeed,
            CallbackInfoReturnable<ItemAttributeModifiers> cir) {
        cir.setReturnValue(ItemAttributeModifiers.EMPTY);
    }
}
