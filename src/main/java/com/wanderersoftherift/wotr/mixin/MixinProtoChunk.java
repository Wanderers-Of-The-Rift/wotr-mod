package com.wanderersoftherift.wotr.mixin;

import net.minecraft.core.Registry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import net.minecraft.world.ticks.ProtoChunkTicks;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Mixin(ProtoChunk.class)
public class MixinProtoChunk {

    @Mutable
    @Shadow
    @Final
    private List<Entity> entities;

    @Coerce
    @Inject(method = "<init>(Lnet/minecraft/world/level/ChunkPos;Lnet/minecraft/world/level/chunk/UpgradeData;[Lnet/minecraft/world/level/chunk/LevelChunkSection;Lnet/minecraft/world/ticks/ProtoChunkTicks;Lnet/minecraft/world/ticks/ProtoChunkTicks;Lnet/minecraft/world/level/LevelHeightAccessor;Lnet/minecraft/core/Registry;Lnet/minecraft/world/level/levelgen/blending/BlendingData;)V", at = @At("TAIL"))
    private void useConcurrentHashMap(
            ChunkPos chunkPos,
            UpgradeData upgradeData,
            LevelChunkSection[] sections,
            ProtoChunkTicks blockTicks,
            ProtoChunkTicks liquidTicks,
            LevelHeightAccessor levelHeightAccessor,
            Registry biomeRegistry,
            BlendingData blendingData,
            CallbackInfo ci) {
        entities = new CopyOnWriteArrayList<>();
    }
}
