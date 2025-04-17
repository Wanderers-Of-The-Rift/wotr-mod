package com.wanderersoftherift.wotr.mixin;

import net.minecraft.server.level.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.IntSupplier;

@Mixin(ChunkMap.class)
public class MixinChunkMap {

    @Shadow @Final ServerLevel level;

    @Redirect(method = "runGenerationTask", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ChunkTaskDispatcher;submit(Ljava/lang/Runnable;JLjava/util/function/IntSupplier;)V"))
    private void submitToVirtualThread(ChunkTaskDispatcher instance, Runnable task, long chunkPos, IntSupplier queueLevelSupplier){
        if (this.level.dimension().location().getNamespace().equals("wotr")) Thread.startVirtualThread(task); else instance.submit(task, chunkPos, queueLevelSupplier);
    }

}
