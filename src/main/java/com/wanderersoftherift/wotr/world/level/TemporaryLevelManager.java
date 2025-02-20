package com.wanderersoftherift.wotr.world.level;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.ModBlocks;
import com.wanderersoftherift.wotr.network.S2CLevelListUpdatePacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.LevelStem;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TemporaryLevelManager {
    private static final List<TemporaryLevel> levels = new ArrayList<>();

    // TODO: deregister and delete files after exiting rift
    public static TemporaryLevel createRiftLevel(ResourceKey<Level> portalDimension, BlockPos portalPos) {
        // Blockpos until we store dimension id in the portal
        ResourceLocation id = WanderersOfTheRift.id("rift_" + portalPos.getX() + "_" + portalPos.getY() + "_" + portalPos.getZ()/* UUID.randomUUID()*/);
        return createRiftLevel(id, portalDimension, portalPos);
    }

    @SuppressWarnings("deprecation")
    public static TemporaryLevel createRiftLevel(ResourceLocation id, ResourceKey<Level> portalDimension, BlockPos portalPos) {
        var server = ServerLifecycleHooks.getCurrentServer();
        var ow = server.overworld();

        Optional<Registry<Level>> dimensionRegistry = ow.registryAccess().lookup(Registries.DIMENSION);
        if (dimensionRegistry.isEmpty()) {
            return null;
        }

        ChunkGenerator chunkGen = getRiftChunkGenerator();

        var stem = getLevelStem(server, id, chunkGen);
        if (stem == null) {
            return null;
        }

        TemporaryLevel level = TemporaryLevel.create(id, stem, portalDimension, portalPos);

        Registry<Level> registry = dimensionRegistry.get();
        if (registry instanceof MappedRegistry<Level> mappedRegistry) {
            mappedRegistry.unfreeze(false);
            if (registry.get(id).isEmpty()) {
                Registry.register(registry, id, level);
            }
            mappedRegistry.freeze();
        }

        level.getServer().forgeGetWorldMap().put(level.dimension(), level);
        level.getServer().markWorldsDirty();
        NeoForge.EVENT_BUS.post(new LevelEvent.Load(level));
        PacketDistributor.sendToAllPlayers(new S2CLevelListUpdatePacket(id, false));
        TemporaryLevelManager.levels.add(level);
        level.setBlock(new BlockPos(0, -1, 0), ModBlocks.RIFT_PORTAL_BLOCK.get().defaultBlockState(), 3);
        return level;
    }

    @SuppressWarnings("deprecation")
    private static LevelStem getLevelStem(MinecraftServer server, ResourceLocation id, ChunkGenerator chunkGen) {
        Optional<Registry<LevelStem>> levelStemRegistry = server.overworld().registryAccess().lookup(Registries.LEVEL_STEM);
        if (levelStemRegistry.isEmpty()) {
            return null;
        }

        var riftType = server.registryAccess().lookupOrThrow(Registries.DIMENSION_TYPE)
            .get(RiftDimensionType.RIFT_DIMENSION_TYPE).get();
        var stem = new LevelStem(riftType, chunkGen);
        var stemRegistry = levelStemRegistry.get();
        if (stemRegistry instanceof MappedRegistry<LevelStem> mappedStemRegistry) {
            mappedStemRegistry.unfreeze(false);
            if (stemRegistry.get(id).isEmpty()) {
                Registry.register(stemRegistry, id, stem);
            }
            mappedStemRegistry.freeze();
        }
        return stem;
    }

    @SuppressWarnings("deprecation")
    public static void unregisterLevel(TemporaryLevel level) {
        level.save(null, true, false);
        level.getServer().forgeGetWorldMap().remove(level.dimension());
        NeoForge.EVENT_BUS.post(new LevelEvent.Unload(level));
        ResourceLocation id = level.getId();
        PacketDistributor.sendToAllPlayers(new S2CLevelListUpdatePacket(id, true));
        level.getServer().markWorldsDirty();
        TemporaryLevelManager.levels.remove(level);
    }

    public static void deleteLevel(TemporaryLevel level) {
    }

    private static ChunkGenerator getRiftChunkGenerator() {
        return new PocRiftChunkGenerator(ResourceLocation.withDefaultNamespace("melon"));
    }
}
