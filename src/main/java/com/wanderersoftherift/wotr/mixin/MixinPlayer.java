package com.wanderersoftherift.wotr.mixin;

import com.wanderersoftherift.wotr.mixinextension.InvulnerabilityCancelable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class MixinPlayer implements InvulnerabilityCancelable {

    @Inject(method = "actuallyHurt", at = @At("HEAD"), cancellable = true)
    private void cancelIFrame(ServerLevel level, DamageSource damageSource, float amount, CallbackInfo ci) {
        cancelInvulnerability(damageSource, ci);
    }

}
