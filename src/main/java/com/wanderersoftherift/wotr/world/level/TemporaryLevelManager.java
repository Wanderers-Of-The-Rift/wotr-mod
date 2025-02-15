package com.wanderersoftherift.wotr.world.level;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.ModBlocks;
import com.wanderersoftherift.wotr.network.S2CLevelListUpdatePacket;
import com.wanderersoftherift.wotr.mixin.AccessorMinecraftServer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
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
import java.util.UUID;

public class TemporaryLevelManager {
    private static final List<TemporaryLevel> levels = new ArrayList<>();

    // TODO: persistent dimensions (DO NOT SHUT DOWN THE SERVER IN RIFT - you won't be able to rejoin)
    // TODO: deregister and delete files after exiting rift
    public static TemporaryLevel createRiftLevel() {
        ResourceLocation id = WanderersOfTheRift.id("rift_" + UUID.randomUUID());

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

        //TODO: figure out how to use the dimension type
        var stem = new LevelStem(Holder.direct(ow.dimensionType()),chunkGen);
        var stemRegistry = levelStemRegistry.get();
        if (stemRegistry instanceof MappedRegistry<LevelStem> mappedStemRegistry) {
            mappedStemRegistry.unfreeze(false);
        }
        Registry.register(stemRegistry, id, stem);

        if (stemRegistry instanceof MappedRegistry<LevelStem> mappedStemRegistry) {
            mappedStemRegistry.freeze();
        }

        TemporaryLevel level = TemporaryLevel.create(id, stem);

        Registry<Level> registry = dimensionRegistry.get();
        if (registry instanceof MappedRegistry<Level> mappedRegistry) {
            mappedRegistry.unfreeze(false);
        }
        if (registry.get(id).isEmpty()){
            Registry.register(registry, id, level);
        }
        if (registry instanceof MappedRegistry<Level> mappedRegistry) {
            mappedRegistry.freeze();
        }

        ((AccessorMinecraftServer)level.getServer()).getLevels().put(level.dimension(), level);
        level.getServer().markWorldsDirty();
        NeoForge.EVENT_BUS.post(new LevelEvent.Load(level));
        PacketDistributor.sendToAllPlayers(new S2CLevelListUpdatePacket(id, false));
        TemporaryLevelManager.levels.add(level);
        level.setBlock(new BlockPos(0,-64,0), ModBlocks.RIFT_PORTAL_BLOCK.get().defaultBlockState(), 3);
        return level;
    }

    public static void unregisterLevel(TemporaryLevel level) {
        // idk how to deregister stuff
        ((AccessorMinecraftServer)level.getServer()).getLevels().remove(level.dimension());
        NeoForge.EVENT_BUS.post(new LevelEvent.Unload(level));
        ResourceLocation id = level.getId();
        PacketDistributor.sendToAllPlayers(new S2CLevelListUpdatePacket(id, true));
        TemporaryLevelManager.levels.remove(level);
    }

    public static void deleteLevel(TemporaryLevel level) {
    }

    private static ChunkGenerator getRiftChunkGenerator(){
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
