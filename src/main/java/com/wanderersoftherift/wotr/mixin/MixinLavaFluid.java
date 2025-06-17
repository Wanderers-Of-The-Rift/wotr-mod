package com.wanderersoftherift.wotr.mixin;

import com.wanderersoftherift.wotr.core.rift.RiftLevelManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.LavaFluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LavaFluid.class)
public class MixinLavaFluid {

    @Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
    private void wotr$OnTickFireBlock(
            ServerLevel serverLevel,
            BlockPos blockPos,
            FluidState fluidState,
            RandomSource randomSource,
            CallbackInfo ci) {
        if (RiftLevelManager.isRift(serverLevel)) {
            ci.cancel();
        }
    }
}
