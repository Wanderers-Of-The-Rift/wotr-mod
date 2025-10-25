package com.wanderersoftherift.wotr.mixin;

import com.wanderersoftherift.wotr.client.tooltip.RunegemTooltipRenderer;
import net.minecraft.client.gui.ItemSlotMouseAction;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public abstract class MixinAbstractContainerScreen {

    @Shadow
    protected abstract void addItemSlotMouseAction(ItemSlotMouseAction itemSlotMouseAction);

    @Inject(method = "init", at = @At("TAIL"))
    private void addAdditionalSlotMouseActions(CallbackInfo ci) {
        addItemSlotMouseAction(new RunegemTooltipRenderer.RunegemMouseActions());
    }
}
