package com.wanderersoftherift.wotr.mixin;

import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.level.progress.ChunkProgressListenerFactory;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.WorldData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.concurrent.Executor;


@Mixin(MinecraftServer.class)
public interface AccessorMinecraftServer {
    @Accessor
    LayeredRegistryAccess<RegistryLayer> getRegistries();

    @Accessor
    Executor getExecutor();

    @Accessor
    LevelStorageSource.LevelStorageAccess getStorageSource();

    @Accessor
    WorldData getWorldData();

    @Accessor
    ChunkProgressListenerFactory getProgressListenerFactory();
}
