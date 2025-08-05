package com.wanderersoftherift.wotr.core.rift;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.entity.portal.PortalSpawnLocation;
import com.wanderersoftherift.wotr.entity.portal.RiftPortalExitEntity;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.init.WotrEntities;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.init.WotrTags;
import com.wanderersoftherift.wotr.item.riftkey.RiftConfig;
import com.wanderersoftherift.wotr.mixin.AccessorMappedRegistry;
import com.wanderersoftherift.wotr.mixin.AccessorMinecraftServer;
import com.wanderersoftherift.wotr.network.rift.S2CLevelListUpdatePacket;
import com.wanderersoftherift.wotr.util.RandomSourceFromJavaRandom;
import com.wanderersoftherift.wotr.world.level.FastRiftGenerator;
import com.wanderersoftherift.wotr.world.level.RiftDimensionType;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.LayeredFiniteRiftLayout;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.RiftLayout;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.layers.ChaosLayer;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.layers.PredefinedRoomLayer;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.layers.RingLayer;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.shape.BoxedRiftShape;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.shape.CoarseDiamondRiftShape;
import com.wanderersoftherift.wotr.world.level.levelgen.template.randomizers.RoomRandomizerImpl;
import com.wanderersoftherift.wotr.world.level.levelgen.theme.RiftTheme;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.RandomSequences;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.DerivedLevelData;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Static manager for handing access to, creation, and destruction of a rift
 */
public final class RiftLevelManager {

    public static final int DEFAULT_RIFT_HEIGHT_IN_CHUNKS = 24;

    private RiftLevelManager() {
    }

    /**
     * @param id
     * @return Whether a level with the given id exists
     */
    public static boolean levelExists(ResourceLocation id) {
        return ServerLifecycleHooks.getCurrentServer()
                .forgeGetWorldMap()
                .containsKey(ResourceKey.create(Registries.DIMENSION, id));
    }

    /**
     * @param level
     * @return Whether the level is a rift
     */
    public static boolean isRift(Level level) {
        Registry<DimensionType> dimTypes = level.registryAccess().lookupOrThrow(Registries.DIMENSION_TYPE);
        Stream<Holder.Reference<DimensionType>> riftTypes = RiftDimensionType.RIFT_DIMENSION_TYPE_MAP.values()
                .stream()
                .map(dimTypes::get)
                .filter(Optional::isPresent)
                .map(Optional::get);
        return riftTypes.anyMatch(dimensionTypeReference -> dimensionTypeReference.value() == level.dimensionType());
    }

    /**
     * @param id
     * @return The rift level with the given id, if it exists
     */
    public static @Nullable ServerLevel getRiftLevel(ResourceLocation id) {
        return getRiftLevel(ResourceKey.create(Registries.DIMENSION, id));
    }

    public static @Nullable ServerLevel getRiftLevel(ResourceKey<Level> id) {
        var server = ServerLifecycleHooks.getCurrentServer();

        ServerLevel serverLevel = server.forgeGetWorldMap().get(id);
        if (serverLevel != null && isRift(serverLevel)) {
            return serverLevel;
        }
        return null;
    }

    public static boolean onPlayerDeath(ServerPlayer player, LivingDeathEvent event) {
        RiftEntryState lastDeathRiftEntryStata = null;
        var riftEntryStates = player.getData(WotrAttachments.RIFT_ENTRY_STATES);
        var topOfTheStackIndex = riftEntryStates.size() - 1;
        for (var riftEntryStateIndex = topOfTheStackIndex; riftEntryStateIndex >= 0; riftEntryStateIndex--) {
            var riftEntryState = riftEntryStates.get(riftEntryStateIndex);
            var level = getRiftLevel(riftEntryState.riftDimension());
            if (level == null) {
                riftEntryStates.remove(riftEntryStateIndex);
                continue;
            }
            var riftData = RiftData.get(level);

            if (riftData.containsPlayer(player) && NeoForge.EVENT_BUS.post(new RiftEvent.PlayerDied(player, level,
                    riftData.getConfig(), event.getSource(), topOfTheStackIndex == riftEntryStateIndex)).isCanceled()) {
                break;
            }
            lastDeathRiftEntryStata = riftEntryState;
            riftEntryStates.remove(riftEntryStateIndex);
            riftData.removePlayer(player);
            if (riftData.isRiftEmpty()) {
                unregisterAndDeleteLevel(level);
            }
        }
        if (lastDeathRiftEntryStata != null) {
            player.setData(WotrAttachments.DEATH_RIFT_ENTRY_STATE, lastDeathRiftEntryStata);
        } else {
            player.setData(WotrAttachments.DEATH_RIFT_ENTRY_STATE, RiftEntryState.EMPTY);
        }
        return riftEntryStates.size() != 0 && lastDeathRiftEntryStata != null;
    }

    /**
     * @param player The player to remove from a rift level
     * @return Whether the player was successfully removed from a rift
     */
    public static boolean returnPlayerFromRift(ServerPlayer player) {
        ServerLevel playerLevel = player.serverLevel();

        var riftEntryStates = player.getData(WotrAttachments.RIFT_ENTRY_STATES);
        if (riftEntryStates.isEmpty()) {
            return false;
        }
        var riftEntryStata = riftEntryStates.removeLast();
        ServerLevel riftLevel = RiftLevelManager.getRiftLevel(riftEntryStata.riftDimension());
        if (!isRift(riftLevel)) {
            return false;
        }

        RiftData riftData = RiftData.get(riftLevel);

        ResourceKey<Level> respawnKey = riftEntryStata.previousDimension();
        if (!riftEntryStata.riftDimension().equals(playerLevel.dimension())) {
            WanderersOfTheRift.LOGGER.error("player {} is tying to leave rift {} but is in {}",
                    player.getName().getString(), riftEntryStata.riftDimension().location(),
                    playerLevel.dimension().location());
        }
        var server = playerLevel.getServer();
        ServerLevel respawnDimension = server.getLevel(respawnKey);
        if (respawnDimension == null) {
            WanderersOfTheRift.LOGGER.error("dimension {} not found, teleporting to overworld instead", respawnKey);
            respawnDimension = server.overworld();
        }

        if (!riftData.containsPlayer(player)) {
            return false;
        }

        var respawnPos = riftEntryStata.previousPosition();
        player.setData(WotrAttachments.EXITED_RIFT_ENTRY_STATE, riftEntryStata);
        riftData.removePlayer(player);
        player.teleportTo(respawnDimension, respawnPos.x(), respawnPos.y(), respawnPos.z(), Set.of(),
                player.getRespawnAngle(), 0, true);
        if (riftData.isRiftEmpty()) {
            RiftLevelManager.unregisterAndDeleteLevel(playerLevel);
        }
        return true;
    }

    // TODO: unload the dimensions if all players are disconnected, but still in the dimension
    @SuppressWarnings("deprecation")
    public static ServerLevel getOrCreateRiftLevel(
            ResourceLocation id,
            ResourceKey<Level> portalDimension,
            BlockPos portalPos,
            RiftConfig config) {
        var server = ServerLifecycleHooks.getCurrentServer();
        var overworld = server.overworld();

        var existingRift = server.forgeGetWorldMap().get(ResourceKey.create(Registries.DIMENSION, id));
        if (existingRift != null) {
            return existingRift;
        }

        Optional<Registry<Level>> dimensionRegistry = server.registryAccess().lookup(Registries.DIMENSION);
        if (dimensionRegistry.isEmpty()) {
            return null;
        }

        config = initializeConfig(config, server);
        var loadedRiftHeight = config.layout()
                .map(fac -> fac.riftShape().levelCount() + FastRiftGenerator.MARGIN_LAYERS);
        if (loadedRiftHeight.isEmpty()) {
            WanderersOfTheRift.LOGGER.error("missing values in RiftConfig");
            return null;
        }
        int requestedRiftHeightChunks = loadedRiftHeight.get();
        var riftDimensionType = getRiftDimensionTypeForHeight(requestedRiftHeightChunks);
        int actualRiftHeight = riftDimensionType.getKey();

        ChunkGenerator chunkGen = getRiftChunkGenerator(overworld, requestedRiftHeightChunks, actualRiftHeight, config);
        if (chunkGen == null) {
            return null;
        }

        var stem = getLevelStem(server, id, chunkGen, riftDimensionType.getValue());
        if (stem == null) {
            return null;
        }

        ServerLevel level = createRift(id, stem, portalDimension, portalPos, config);

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

        NeoForge.EVENT_BUS.post(new RiftEvent.Created(level, config));
        NeoForge.EVENT_BUS.post(new LevelEvent.Load(level));

        PacketDistributor.sendToAllPlayers(new S2CLevelListUpdatePacket(id, false));
        spawnRiftExit(level, PortalSpawnLocation.DEFAULT_RIFT_EXIT_POSITION.above().getBottomCenter());
        WanderersOfTheRift.LOGGER.debug("Created rift level {}", id);
        return level;
    }

    private static Map.Entry<Integer, ResourceKey<DimensionType>> getRiftDimensionTypeForHeight(
            int requestedRiftHeightChunks) {
        return RiftDimensionType.RIFT_DIMENSION_TYPE_MAP
                .ceilingEntry(requestedRiftHeightChunks * LevelChunkSection.SECTION_HEIGHT);
    }

    /**
     * copy of {@link com.wanderersoftherift.wotr.item.riftkey.RiftKey::spawnRift(Level, Vec3, Direction)}
     */
    // TODO: clean it up (maybe move as static method to the entity or the spawner class)
    private static void spawnRiftExit(Level level, Vec3 pos) {
        RiftPortalExitEntity rift = new RiftPortalExitEntity(WotrEntities.RIFT_EXIT.get(), level);
        rift.setPos(pos);
        rift.setYRot(Direction.UP.toYRot());
        rift.setBillboard(true);
        level.addFreshEntity(rift);
    }

    @SuppressWarnings("deprecation")
    private static LevelStem getLevelStem(
            MinecraftServer server,
            ResourceLocation id,
            ChunkGenerator chunkGen,
            ResourceKey<DimensionType> type) {
        Optional<Registry<LevelStem>> levelStemRegistry = server.overworld()
                .registryAccess()
                .lookup(Registries.LEVEL_STEM);
        if (levelStemRegistry.isEmpty()) {
            return null;
        }

        var riftType = server.registryAccess().lookupOrThrow(Registries.DIMENSION_TYPE).get(type).orElse(null);
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
    private static void unregisterAndDeleteLevel(ServerLevel level) {
        if (!isRift(level)) {
            return;
        }
        RiftData riftData = RiftData.get(level);
        if (!riftData.isRiftEmpty()) {
            // multiplayer - delete after all players leave
            return;
        }

        NeoForge.EVENT_BUS.post(new RiftEvent.Closing(level, riftData.getConfig()));
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
                var accessorMappedRegistry = (AccessorMappedRegistry<Level>) mr;
                Holder.Reference<Level> holder = accessorMappedRegistry.getByLocation().remove(id);
                if (holder == null) {
                    WanderersOfTheRift.LOGGER.error("Failed to remove level from registry (null holder)");
                    return;
                }
                int dimId = mr.getId(level.dimension());
                if (dimId == -1) {
                    WanderersOfTheRift.LOGGER.error("Failed to remove level from registry (id -1)");
                    return;
                }
                accessorMappedRegistry.getToId().remove(holder.value());
                accessorMappedRegistry.getById().set(dimId, null);
                accessorMappedRegistry.getByKey().remove(holder.key());
                accessorMappedRegistry.getByValue().remove(holder.value());
                accessorMappedRegistry.getRegistrationInfos().remove(holder.key());
            }
        });
        level.getServer().overworld().save(null, true, false);
    }

    private static RiftConfig initializeConfig(RiftConfig baseConfig, MinecraftServer server) {
        var random = RandomSource.create();
        int seed = baseConfig.seed().orElseGet(random::nextInt);
        var riftTheme = baseConfig.theme().orElse(getRandomTheme(server, seed));
        return new RiftConfig(baseConfig.tier(), Optional.of(riftTheme), baseConfig.objective(),
                baseConfig.layout().isPresent() ? baseConfig.layout()
                        : Optional.of(
                                defaultLayout(baseConfig.tier(), seed)),
                Optional.of(seed));
    }

    private static RiftLayout.Factory defaultLayout(int tier, int seed) {
        var layerCount = DEFAULT_RIFT_HEIGHT_IN_CHUNKS - FastRiftGenerator.MARGIN_LAYERS;
        var factory = new LayeredFiniteRiftLayout.Factory(
                new BoxedRiftShape(new CoarseDiamondRiftShape(2 + tier * 3, 3.0, layerCount),
                        new Vec3i(-1 - 3 * tier, -layerCount / 2, -1 - 3 * tier),
                        new Vec3i(3 + 6 * tier, layerCount, 3 + 6 * tier)),
                Optional.of(seed), List.of(
                        new PredefinedRoomLayer.Factory(
                                new RoomRandomizerImpl.Factory(WanderersOfTheRift.id("rift/room_portal"),
                                        RoomRandomizerImpl.SINGLE_SIZE_SPACE_HOLDER_FACTORY),
                                new Vec3i(-1, -1, -1)),
                        new RingLayer.Factory(new RoomRandomizerImpl.Factory(WanderersOfTheRift.id("rift/room_stable"),
                                RoomRandomizerImpl.SINGLE_SIZE_SPACE_HOLDER_FACTORY), 5),
                        new RingLayer.Factory(
                                new RoomRandomizerImpl.Factory(WanderersOfTheRift.id("rift/room_unstable"),
                                        RoomRandomizerImpl.SINGLE_SIZE_SPACE_HOLDER_FACTORY),
                                10),
                        new ChaosLayer.Factory(new RoomRandomizerImpl.Factory(WanderersOfTheRift.id("rift/room_chaos"),
                                RoomRandomizerImpl.MULTI_SIZE_SPACE_HOLDER_FACTORY))

                ));
        return factory;
    }

    private static ChunkGenerator getRiftChunkGenerator(
            ServerLevel overworld,
            int layerCount,
            int dimensionHeightBlocks,
            RiftConfig config) {
        var voidBiome = overworld.registryAccess().lookupOrThrow(Registries.BIOME).get(Biomes.THE_VOID).orElse(null);
        if (voidBiome == null) {
            return null;
        }
        return new FastRiftGenerator(new FixedBiomeSource(voidBiome), layerCount, dimensionHeightBlocks,
                ResourceLocation.withDefaultNamespace("bedrock"), config);
    }

    private static ServerLevel createRift(
            ResourceLocation id,
            LevelStem stem,
            ResourceKey<Level> portalDimension,
            BlockPos portalPos,
            RiftConfig config) {
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

        int seed = config.seed().orElseGet(() -> new Random().nextInt());

        var riftLevel = new ServerLevel(ServerLifecycleHooks.getCurrentServer(), executor, storageSource,
                new DerivedLevelData(worldData, worldData.overworldData()),
                ResourceKey.create(Registries.DIMENSION, id), stem, chunkProgressListener, false, 0L, List.of(), false,
                RandomSequences.factory(seed).constructor().get());
        var riftData = RiftData.get(riftLevel);
        riftData.setPortalDimension(portalDimension);
        riftData.setPortalPos(portalPos);
        riftData.setConfig(config);

        riftData.setTheme(config.theme()
                /* note: cannot be empty at this point */.orElse(getRandomTheme(riftLevel.getServer(), seed)));

        return riftLevel;
    }

    public static Holder<RiftTheme> getRandomTheme(MinecraftServer server, int seed) {

        var themeRandom = new RandomSourceFromJavaRandom(RandomSourceFromJavaRandom.get(0),
                seed * 5624397638181617163L);

        Registry<RiftTheme> registry = server.registryAccess().lookupOrThrow(WotrRegistries.Keys.RIFT_THEMES);

        return registry.getRandomElementOf(WotrTags.RiftThemes.RANDOM_SELECTABLE, themeRandom)
                .orElseThrow(() -> new IllegalStateException("No rift themes available"));
    }
}
