package com.wanderersoftherift.wotr.mixin;

import com.wanderersoftherift.wotr.modifier.ModifierHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public class MixinServerPlayer {

    @Inject(method = "setGameMode", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;runLocationChangedEffects(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LivingEntity;)V"))
    private void onSetGameModeRunLocationChangedEffects(GameType gameMode, CallbackInfoReturnable<Boolean> cir) {
        ServerPlayer player = (ServerPlayer) (Object) this;
        ModifierHelper.enableModifier(player);
    }

    @Inject(method = "setGameMode", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;stopLocationBasedEffects(Lnet/minecraft/world/entity/LivingEntity;)V"))
    private void onSetGameModeStopLocationBasedEffects(GameType gameMode, CallbackInfoReturnable<Boolean> cir) {
        ServerPlayer player = (ServerPlayer) (Object) this;
        ModifierHelper.disableModifier(player);
    }
}
