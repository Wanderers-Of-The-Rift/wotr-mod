package com.wanderersoftherift.wotr.core.rift;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.entity.portal.RiftPortalExitEntity;
import com.wanderersoftherift.wotr.init.ModDataComponentType;
import com.wanderersoftherift.wotr.init.ModEntities;
import com.wanderersoftherift.wotr.mixin.AccessorMappedRegistry;
import com.wanderersoftherift.wotr.mixin.AccessorMinecraftServer;
import com.wanderersoftherift.wotr.network.S2CLevelListUpdatePacket;
import com.wanderersoftherift.wotr.world.level.RiftDimensionType;
import com.wanderersoftherift.wotr.world.level.SingleBlockGenerator;
import com.wanderersoftherift.wotr.world.level.levelgen.theme.LevelRiftThemeData;
import com.wanderersoftherift.wotr.world.level.levelgen.theme.RiftTheme;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.RandomSequences;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.storage.DerivedLevelData;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.apache.commons.io.FileUtils;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class RiftLevelManager {

    public static boolean isRiftExists(ResourceLocation id) {
        var server = ServerLifecycleHooks.getCurrentServer();
        var existingRift = server.forgeGetWorldMap().get(ResourceKey.create(Registries.DIMENSION, id));
        return existingRift != null;
    }

    // TODO: unload the dimesnions if all plauers are disconnected, but still in the dimension
    @SuppressWarnings("deprecation")
    public static ServerLevel getOrCreateRiftLevel(ResourceLocation id, ResourceKey<Level> portalDimension,
            BlockPos portalPos, @Nullable ItemStack riftKey) {
        var server = ServerLifecycleHooks.getCurrentServer();
        var ow = server.overworld();

        var existingRift = server.forgeGetWorldMap().get(ResourceKey.create(Registries.DIMENSION, id));
        if (existingRift != null) {
            WanderersOfTheRift.LOGGER.debug("Found existing rift level {}", id);
            return existingRift;
        }

        Optional<Registry<Level>> dimensionRegistry = ow.registryAccess().lookup(Registries.DIMENSION);
        if (dimensionRegistry.isEmpty()) {
            return null;
        }

        ChunkGenerator chunkGen = getRiftChunkGenerator(ow);
        if (chunkGen == null) {
            return null;
        }

        var stem = getLevelStem(server, id, chunkGen);
        if (stem == null) {
            return null;
        }

        ServerLevel level = createRift(id, stem, portalDimension, portalPos, riftKey);

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
        spawnRift(id, riftKey, level, new BlockPos(0, 0, 0).above().getBottomCenter(), Direction.UP);
        WanderersOfTheRift.LOGGER.debug("Created rift level {}", id);
        return level;
    }

    /** copy of {@link com.wanderersoftherift.wotr.item.riftkey.RiftKey::spawnRift(Level, Vec3, Direction)} */
    // TODO: clean it up (maybe move as static method to the entity or the spawner class)
    private static void spawnRift(ResourceLocation id, ItemStack riftKey, Level level, Vec3 pos, Direction dir) {
        RiftPortalExitEntity rift = new RiftPortalExitEntity(ModEntities.RIFT_ENTRANCE.get(), level);
        rift.setPos(pos);
        rift.setYRot(dir.toYRot());
        rift.setBillboard(dir.getAxis().isVertical());
        rift.setRiftDimensionID(id);
        rift.setRiftkey(riftKey);
        level.addFreshEntity(rift);
    }

    @SuppressWarnings("deprecation")
    private static LevelStem getLevelStem(MinecraftServer server, ResourceLocation id, ChunkGenerator chunkGen) {
        Optional<Registry<LevelStem>> levelStemRegistry = server.overworld()
                .registryAccess()
                .lookup(Registries.LEVEL_STEM);
        if (levelStemRegistry.isEmpty()) {
            return null;
        }

        var riftType = server.registryAccess()
                .lookupOrThrow(Registries.DIMENSION_TYPE)
                .get(RiftDimensionType.RIFT_DIMENSION_TYPE)
                .orElse(null);
        if (riftType == null) {
            WanderersOfTheRift.LOGGER.error("Failed to get rift dimension type");
            return null;
        }
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

    @SuppressWarnings({ "unchecked", "deprecation" })
    public static void unregisterAndDeleteLevel(ServerLevel level) {
        if (!RiftData.isRift(level)) {
            return;
        }
        if (!RiftData.get(level).getPlayers().isEmpty()) {
            // multiplayer - delete after all players leave
            return;
        }

        // unload the level
        level.save(null, true, false);
        level.getServer().forgeGetWorldMap().remove(level.dimension());
        NeoForge.EVENT_BUS.post(new LevelEvent.Unload(level));
        ResourceLocation id = level.dimension().location();
        PacketDistributor.sendToAllPlayers(new S2CLevelListUpdatePacket(id, true));
        level.getServer().markWorldsDirty();

        // Delete level files - we might need to move this to end of tick because ticking (block)entities might have
        // references to the level
        var dimPath = ((AccessorMinecraftServer) level.getServer()).getStorageSource()
                .getDimensionPath(level.dimension());
        if (Files.exists(dimPath)) {
            WanderersOfTheRift.LOGGER.info("Deleting level {}", dimPath);
            try {
                FileUtils.deleteDirectory(dimPath.toFile());
            } catch (IOException e) {
                WanderersOfTheRift.LOGGER.error("Failed to delete level", e);
            }
        }

        // dimensions are also saved in level.dat
        // this monstrosity deletes them from the registry to prevent reloading them on next server start
        level.getServer().registryAccess().lookupOrThrow(Registries.DIMENSION).get(level.dimension()).ifPresent(dim -> {
            if (level.getServer()
                    .registryAccess()
                    .lookupOrThrow(Registries.DIMENSION) instanceof MappedRegistry<Level> mr) {
                Holder.Reference<Level> holder = ((AccessorMappedRegistry<Level>) mr).getByLocation().remove(id);
                if (holder == null) {
                    WanderersOfTheRift.LOGGER.error("Failed to remove level from registry (null holder)");
                    return;
                }
                int dimId = mr.getId(level.dimension());
                if (dimId == -1) {
                    WanderersOfTheRift.LOGGER.error("Failed to remove level from registry (id -1)");
                    return;
                }
                ((AccessorMappedRegistry<Level>) mr).getToId().remove(holder.value());
                ((AccessorMappedRegistry<Level>) mr).getById().set(dimId, null);
                ((AccessorMappedRegistry<Level>) mr).getByKey().remove(holder.key());
                ((AccessorMappedRegistry<Level>) mr).getByValue().remove(holder.value());
                ((AccessorMappedRegistry<Level>) mr).getRegistrationInfos().remove(holder.key());
            }
        });
        level.getServer().overworld().save(null, true, false);
    }

    private static ChunkGenerator getRiftChunkGenerator(ServerLevel overworld) {
        var voidBiome = overworld.registryAccess().lookupOrThrow(Registries.BIOME).get(Biomes.THE_VOID).orElse(null);
        if (voidBiome == null) {
            return null;
        }
        return new SingleBlockGenerator(new FixedBiomeSource(voidBiome),
                ResourceLocation.withDefaultNamespace("bedrock"));
    }

    private static ServerLevel createRift(ResourceLocation id, LevelStem stem, ResourceKey<Level> portalDimension,
            BlockPos portalPos, @Nullable ItemStack riftKey) {
        AccessorMinecraftServer server = (AccessorMinecraftServer) ServerLifecycleHooks.getCurrentServer();
        var chunkProgressListener = server.getProgressListenerFactory().create(0);
        var storageSource = server.getStorageSource();
        var worldData = server.getWorldData();
        var executor = server.getExecutor();

        if (portalDimension == null || portalPos == null) {
            WanderersOfTheRift.LOGGER.warn(
                    "Tried to create rift {} with portal from dimension {} at position {}, using overworld spawnpoint instead.",
                    id, portalDimension, portalPos);
            portalDimension = Level.OVERWORLD;
            portalPos = ServerLifecycleHooks.getCurrentServer().overworld().getSharedSpawnPos();
        }

        int seed = new Random().nextInt();
        if (riftKey != null) {
            Integer keySeed = riftKey.get(ModDataComponentType.RIFT_SEED);
            if (keySeed != null) {
                seed = keySeed;
            }
        }

        var riftLevel = new ServerLevel(ServerLifecycleHooks.getCurrentServer(), executor, storageSource,
                new DerivedLevelData(worldData, worldData.overworldData()),
                ResourceKey.create(Registries.DIMENSION, id), stem, chunkProgressListener, false, 0L, List.of(), false,
                RandomSequences.factory(seed).constructor().get());
        var riftData = RiftData.get(riftLevel);
        riftData.setPortalDimension(portalDimension);
        riftData.setPortalPos(portalPos);
        var themeData = LevelRiftThemeData.getFromLevel(riftLevel);

        Holder<RiftTheme> riftTheme = null;
        int maxDepth = RiftLevelManager.getRiftSize(null);
        if (riftKey != null) {
            ResourceLocation theme = riftKey.get(ModDataComponentType.RIFT_THEME);
            if (theme != null) {
                riftTheme = LevelRiftThemeData.fromId(theme, riftLevel);
            }
            Integer tier = riftKey.getOrDefault(ModDataComponentType.RIFT_TIER, 0);
            riftData.setTier(tier);
            maxDepth = RiftLevelManager.getRiftSize(tier);
        }
        if (riftTheme == null) {
            riftTheme = LevelRiftThemeData.getRandomTheme(riftLevel);
        }
        themeData.setTheme(riftTheme);

        placeInitialJigsaw(riftLevel, WanderersOfTheRift.id("rift/room_portal"), WanderersOfTheRift.id("portal"),
                maxDepth, new BlockPos(0, 2, 0));
        return riftLevel;
    }

    private static void placeInitialJigsaw(ServerLevel level, ResourceLocation templatePoolKey, ResourceLocation target,
            int maxDepth, BlockPos pos) {
        var templatePool = level.registryAccess()
                .lookupOrThrow(Registries.TEMPLATE_POOL)
                .get(templatePoolKey)
                .orElse(null);
        if (templatePool == null) {
            WanderersOfTheRift.LOGGER.error("Template pool {} not found", templatePoolKey);
            return;
        }
        JigsawPlacement.generateJigsaw(level, templatePool, target, maxDepth, pos, false);
    }

    private static int getRiftSize(Integer tier) {
        if (tier == null) {
            return 5;
        }
        return switch (tier) {
            case 0, 1 -> 5; // no chaos
            case 2 -> 7;
            case 3 -> 9;
            case 4 -> 11;
            case 5 -> 13;
            case 6 -> 15;
            case 7 -> 17;
            default -> 19; // structures are capped at 20 and it has to be odd for POIs to spawn
        };
    }

}
