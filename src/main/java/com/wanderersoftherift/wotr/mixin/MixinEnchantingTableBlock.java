package com.wanderersoftherift.wotr.mixin;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EnchantingTableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/* Currently prevents the opening of the Enchantment Menu, take high priority */
@Mixin(value = EnchantingTableBlock.class, priority = 100)
public class MixinEnchantingTableBlock {

    @Inject(method = "useWithoutItem" , at = @At("HEAD"), cancellable = true)
    private void cancelMenu(BlockState pBlockState, Level pLevel, BlockPos pBlockPos, Player pPlayer, BlockHitResult pBlockHitResult, CallbackInfoReturnable<InteractionResult> cir) {
        if(pPlayer.level().isClientSide()) {
            // Currently a literal because we likely want to change this at some point.
            pPlayer.displayClientMessage(Component.literal(
                    "This Block has been disabled! (We can change this to one of our custom menus potentially too)").withStyle(ChatFormatting.RED),
                    false);
        }
        cir.setReturnValue(InteractionResult.FAIL);
    }
}
