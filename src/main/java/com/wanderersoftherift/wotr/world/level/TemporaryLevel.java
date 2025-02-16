package com.wanderersoftherift.wotr.world.level;

import com.wanderersoftherift.wotr.mixin.AccessorMinecraftServer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.world.RandomSequences;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.DerivedLevelData;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.ServerLevelData;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.List;
import java.util.concurrent.Executor;

public class TemporaryLevel extends ServerLevel {
    private final ResourceLocation id;

    public ResourceKey<Level> getPortalDimension() {
        return portalDimension;
    }

    public BlockPos getPortalPos() {
        return portalPos;
    }

    private final ResourceKey<Level> portalDimension;
    private final BlockPos portalPos;

    private TemporaryLevel(MinecraftServer server, Executor dispatcher, LevelStorageSource.LevelStorageAccess levelStorageAccess,
                           ServerLevelData serverLevelData, ResourceKey<Level> dimension, LevelStem levelStem, ChunkProgressListener progressListener,
                           boolean isDebug, long biomeZoomSeed, List<CustomSpawner> customSpawners, boolean tickTime,
                           RandomSequences randomSequences, ResourceLocation id, ResourceKey<Level> portalDimension, BlockPos portalPos) {
        super(server, dispatcher, levelStorageAccess, serverLevelData, dimension, levelStem, progressListener, isDebug, biomeZoomSeed, customSpawners, tickTime, randomSequences);
        this.id = id;
        this.portalDimension = portalDimension;
        this.portalPos = portalPos;
    }


    public static TemporaryLevel create(ResourceLocation id, LevelStem stem, ResourceKey<Level> portalDimension, BlockPos portalPos) {
        AccessorMinecraftServer server = (AccessorMinecraftServer) ServerLifecycleHooks.getCurrentServer();
        var chunkProgressListener = server.getProgressListenerFactory().create(0);
        var storageSource = server.getStorageSource();
        var worldData = server.getWorldData();
        var executor = server.getExecutor();

        return new TemporaryLevel(
            ServerLifecycleHooks.getCurrentServer(),
            executor,
            storageSource,
            new DerivedLevelData(worldData, worldData.overworldData()),
            ResourceKey.create(Registries.DIMENSION, id),
            stem,
            chunkProgressListener,
            false,
            0L,
            List.of(),
            false,
            ServerLifecycleHooks.getCurrentServer().overworld().getRandomSequences(),
            id,
            portalDimension,
            portalPos
        );
    }

    public ResourceLocation getId() {
        return id;
    }
}
