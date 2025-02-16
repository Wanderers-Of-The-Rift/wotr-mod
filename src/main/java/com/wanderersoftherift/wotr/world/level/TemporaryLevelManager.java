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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.flat.FlatLayerInfo;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TemporaryLevelManager {
    private static final List<TemporaryLevel> levels = new ArrayList<>();

    // TODO: persistent rifts - rift isn't a TemporaryLevel after reload now
    // TODO: deregister and delete files after exiting rift
    @SuppressWarnings("deprecation")
    public static TemporaryLevel createRiftLevel(ResourceKey<Level> portalDimension, BlockPos portalPos) {
        // Blockpos until we store dimension id in the portal
        ResourceLocation id = WanderersOfTheRift.id("rift_" + portalPos.getX() + "_" + portalPos.getY() + "_" + portalPos.getZ()/* UUID.randomUUID()*/);

        var ow = ServerLifecycleHooks.getCurrentServer().overworld();
        Optional<Registry<LevelStem>> levelStemRegistry = ow.registryAccess().lookup(Registries.LEVEL_STEM);
        if (levelStemRegistry.isEmpty()) {
            return null;
        }
        Optional<Registry<Level>> dimensionRegistry = ow.registryAccess().lookup(Registries.DIMENSION);
        if (dimensionRegistry.isEmpty()) {
            return null;
        }

        ChunkGenerator chunkGen = getRiftChunkGenerator();
        if (chunkGen == null) {
            return null;
        }

        var riftType =  ServerLifecycleHooks.getCurrentServer().registryAccess().lookupOrThrow(Registries.DIMENSION_TYPE).get(RiftDimensionType.RIFT_DIMENSION_TYPE).get();
        var stem = new LevelStem(riftType, chunkGen);
        var stemRegistry = levelStemRegistry.get();
        if (stemRegistry instanceof MappedRegistry<LevelStem> mappedStemRegistry) {
            mappedStemRegistry.unfreeze(false);
            if (stemRegistry.get(id).isEmpty()) {
                Registry.register(stemRegistry, id, stem);
            }
            mappedStemRegistry.freeze();
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
        level.setBlock(new BlockPos(0, -64, 0), ModBlocks.RIFT_PORTAL_BLOCK.get().defaultBlockState(), 3);
        return level;
    }

    @SuppressWarnings("deprecation")
    public static void unregisterLevel(TemporaryLevel level) {
        level.save(null, true, false);
        //TODO: what to do with LevelStem?
        level.getServer().forgeGetWorldMap().remove(level);
        NeoForge.EVENT_BUS.post(new LevelEvent.Unload(level));
        ResourceLocation id = level.getId();
        PacketDistributor.sendToAllPlayers(new S2CLevelListUpdatePacket(id, true));
        level.getServer().markWorldsDirty();
        TemporaryLevelManager.levels.remove(level);
    }

    public static void deleteLevel(TemporaryLevel level) {
    }

    private static ChunkGenerator getRiftChunkGenerator() {
        var biomeHolder = ServerLifecycleHooks.getCurrentServer().overworld().registryAccess().lookup(Registries.BIOME).flatMap(x -> x.get(Biomes.THE_VOID)).orElse(null);
        if (biomeHolder == null) {
            return null;
        }
        var flatSettings = new FlatLevelGeneratorSettings(Optional.empty(), biomeHolder, List.of());
        List<FlatLayerInfo> layerInfos = new ArrayList<>();
        layerInfos.add(new FlatLayerInfo(1, Blocks.STONE));
        flatSettings = flatSettings.withBiomeAndLayers(layerInfos, Optional.empty(), biomeHolder);
        return new FlatLevelSource(flatSettings);
    }
}
