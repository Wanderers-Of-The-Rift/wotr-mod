package com.wanderersoftherift.wotr.core.rift;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
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
import java.util.UUID;
import java.util.concurrent.Executor;

public class RiftLevel extends ServerLevel {
    private final ResourceLocation id;
    private final RiftData data;

    private RiftLevel(MinecraftServer server, Executor dispatcher, LevelStorageSource.LevelStorageAccess levelStorageAccess,
                      ServerLevelData serverLevelData, ResourceKey<Level> dimension, LevelStem levelStem, ChunkProgressListener progressListener,
                      boolean isDebug, long biomeZoomSeed, List<CustomSpawner> customSpawners, boolean tickTime,
                      RandomSequences randomSequences, ResourceLocation id, ResourceKey<Level> portalDimension, BlockPos portalPos) {
        super(server, dispatcher, levelStorageAccess, serverLevelData, dimension, levelStem, progressListener, isDebug, biomeZoomSeed, customSpawners, tickTime, randomSequences);
        this.id = id;
        this.data = this.getDataStorage().computeIfAbsent(RiftData.factory(portalDimension, portalPos), "data_"+ id.getPath());
    }


    public static RiftLevel create(ResourceLocation id, LevelStem stem, ResourceKey<Level> portalDimension, BlockPos portalPos) {
        AccessorMinecraftServer server = (AccessorMinecraftServer) ServerLifecycleHooks.getCurrentServer();
        var chunkProgressListener = server.getProgressListenerFactory().create(0);
        var storageSource = server.getStorageSource();
        var worldData = server.getWorldData();
        var executor = server.getExecutor();

        if (portalDimension == null || portalPos == null) {
            WanderersOfTheRift.LOGGER.warn("Tried to create rift {} with portal from dimension {} at position {}, using overworld spawnpoint instead.",id, portalDimension, portalPos);
            portalDimension = Level.OVERWORLD;
            portalPos = ServerLifecycleHooks.getCurrentServer().overworld().getSharedSpawnPos();
        }

        return new RiftLevel(
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
            null,
            id,
            portalDimension,
            portalPos
        );
    }

    public ResourceLocation getId() {
        return id;
    }

    public ResourceKey<Level> getPortalDimension() {
        return data.getPortalDimension();
    }

    public BlockPos getPortalPos() {
        return data.getPortalPos();
    }

    public List<UUID> getPlayers() {
        return data.getPlayers();
    }

    public void removePlayer(UUID player) {
        data.removePlayer(player);
    }

    public void addPlayer(UUID player) {
        data.addPlayer(player);
    }

}
