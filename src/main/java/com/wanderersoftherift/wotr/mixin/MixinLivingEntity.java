package com.wanderersoftherift.wotr.mixin;

import com.wanderersoftherift.wotr.entity.LivingDamageHandledEvent;
import com.wanderersoftherift.wotr.entity.player.LivingAttributeChangedEvent;
import com.wanderersoftherift.wotr.mixinextension.InvulnerabilityCancelable;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity implements InvulnerabilityCancelable {

    private static final int ENVIRONMENT_INVULNERABILITY_LENGTH = 10;
    private int environmentInvulnerability = 0;

    @Inject(method = "onAttributeUpdated", at = @At(value = "TAIL"))
    private void sendPlayerEventOnAttributeUpdated(Holder<Attribute> attribute, CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        NeoForge.EVENT_BUS.post(new LivingAttributeChangedEvent(entity, attribute));
    }

    @Inject(method = "handleDamageEvent", at = @At("HEAD"))
    private void invokeLivingDamageHandledEvent(DamageSource source, CallbackInfo ci) {
        NeoForge.EVENT_BUS.post(new LivingDamageHandledEvent((LivingEntity) (Object) this, source));
    }

    @Inject(method = "hurtServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;actuallyHurt(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)V", shift = At.Shift.AFTER, ordinal = 1), cancellable = true)
    private void noFalseDamage(
            ServerLevel level,
            DamageSource damageSource,
            float amount,
            CallbackInfoReturnable<Boolean> cir) {
        if (damageSource.getEntity() == null && damageSource.getDirectEntity() == null
                && environmentInvulnerability < ENVIRONMENT_INVULNERABILITY_LENGTH) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "actuallyHurt", at = @At("HEAD"), cancellable = true)
    private void cancelIFrame(ServerLevel level, DamageSource damageSource, float amount, CallbackInfo ci) {
        cancelInvulnerability(damageSource, ci);
    }

    @Override
    public void cancelInvulnerability(DamageSource damageSource, CallbackInfo ci) {
        if (damageSource.getEntity() == null && damageSource.getDirectEntity() == null) {
            if (environmentInvulnerability > 0) {
                ci.cancel();
            } else {
                environmentInvulnerability = ENVIRONMENT_INVULNERABILITY_LENGTH;
            }
        }
        ((Entity) (Object) this).invulnerableTime = 0;
    }

    @Inject(method = "baseTick", at = @At("TAIL"))
    private void tickEnvironmentInvulnerability(CallbackInfo ci) {
        if (environmentInvulnerability > 0) {
            environmentInvulnerability--;
        }
    }

}
