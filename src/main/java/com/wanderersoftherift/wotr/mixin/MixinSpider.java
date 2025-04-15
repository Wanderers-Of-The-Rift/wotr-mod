package com.wanderersoftherift.wotr.mixin;

import com.wanderersoftherift.wotr.config.ClientConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin for replacing spider sounds when the arachnophobia accessibility setting is toggled
 */
@Mixin(Spider.class)
public class MixinSpider extends Monster {

    protected MixinSpider(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "getAmbientSound", at = @At("HEAD"), cancellable = true)
    protected void getAltAmbientSound(CallbackInfoReturnable<SoundEvent> cir) {
        if (ClientConfig.ACCESSIBILITY_ARACHNOPHOBIA.getAsBoolean()) {
            cir.setReturnValue(SoundEvents.TURTLE_AMBIENT_LAND);
        }
    }

    @Inject(method = "getHurtSound", at = @At("HEAD"), cancellable = true)
    protected void getAltHurtSound(CallbackInfoReturnable<SoundEvent> cir) {
        if (ClientConfig.ACCESSIBILITY_ARACHNOPHOBIA.getAsBoolean()) {
            cir.setReturnValue(SoundEvents.TURTLE_HURT);
        }
    }

    @Inject(method = "getDeathSound", at = @At("HEAD"), cancellable = true)
    protected void getAltDeathSound(CallbackInfoReturnable<SoundEvent> cir) {
        if (ClientConfig.ACCESSIBILITY_ARACHNOPHOBIA.getAsBoolean()) {
            cir.setReturnValue(SoundEvents.TURTLE_DEATH);
        }
    }

    @Inject(method = "playStepSound", at = @At("HEAD"), cancellable = true)
    protected void playAltStepSound(BlockPos pos, BlockState block, CallbackInfo ci) {
        if (ClientConfig.ACCESSIBILITY_ARACHNOPHOBIA.getAsBoolean()) {
            this.playSound(SoundEvents.TURTLE_SHAMBLE, 0.25F, 1.0F);
            ci.cancel();
        }
    }
}
