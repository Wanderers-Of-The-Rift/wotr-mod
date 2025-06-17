package com.wanderersoftherift.wotr.mixin;

import com.wanderersoftherift.wotr.core.rift.RiftLevelManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FireBlock.class)
public class MixinFireBlock {

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void wotr$OnTickFireBlock(
            BlockState blockState,
            ServerLevel serverLevel,
            BlockPos blockPos,
            RandomSource randomSource,
            CallbackInfo ci) {
        if (RiftLevelManager.isRift(serverLevel)) {
            ci.cancel();
        }
    }
}
