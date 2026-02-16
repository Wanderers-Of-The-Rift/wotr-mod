package com.wanderersoftherift.wotr.mixin;

import com.wanderersoftherift.wotr.loot.InstantLoot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerMenu.class)
public abstract class MixinAbstractContainerMenu {

    @Shadow
    public abstract Slot getSlot(int slotId);

    @Inject(method = "doClick", at = @At("HEAD"), cancellable = true)
    private void instantLootSpecial(int slotId, int button, ClickType clickType, Player player, CallbackInfo ci) {
        if ((clickType == ClickType.QUICK_MOVE || clickType == ClickType.SWAP)
                && InstantLoot.tryConsume(getSlot(slotId).getItem(), player)) {
            ci.cancel();
        }
    }

}
