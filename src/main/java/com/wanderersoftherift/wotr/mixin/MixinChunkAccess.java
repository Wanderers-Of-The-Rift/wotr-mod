package com.wanderersoftherift.wotr.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(ChunkAccess.class)
public class MixinChunkAccess {

    @Mutable
    @Shadow
    @Final
    protected Map<BlockPos, CompoundTag> pendingBlockEntities;

    @Mutable
    @Shadow
    @Final
    protected Map<BlockPos, BlockEntity> blockEntities;

    @Coerce
    @Inject(method = "<init>", at = @At("TAIL"))
    private void useConcurrentHashMap(
            ChunkPos chunkPos,
            UpgradeData upgradeData,
            LevelHeightAccessor levelHeightAccessor,
            Registry registry,
            long inhabitedTime,
            LevelChunkSection[] chunkSections,
            BlendingData blendingData,
            CallbackInfo callbackInfo) {
        pendingBlockEntities = new ConcurrentHashMap<>();
        blockEntities = new ConcurrentHashMap<>();
    }
}
