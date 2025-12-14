package com.wanderersoftherift.wotr.world.level;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.core.rift.RiftConfig;
import com.wanderersoftherift.wotr.core.rift.RiftGenerationConfig;
import com.wanderersoftherift.wotr.init.worldgen.WotrRiftConfigDataTypes;
import com.wanderersoftherift.wotr.mixin.AccessorStructureManager;
import com.wanderersoftherift.wotr.util.RandomFactoryType;
import com.wanderersoftherift.wotr.util.RandomSourceFromJavaRandom;
import com.wanderersoftherift.wotr.world.level.levelgen.RiftProcessedChunk;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.RiftLayout;
import com.wanderersoftherift.wotr.world.level.levelgen.roomgen.RiftRoomGenerator;
import com.wanderersoftherift.wotr.world.level.levelgen.space.RoomRiftSpace;
import com.wanderersoftherift.wotr.world.level.levelgen.space.VoidRiftSpace;
import com.wanderersoftherift.wotr.world.level.levelgen.template.RiftGeneratableId;
import com.wanderersoftherift.wotr.world.level.levelgen.template.SerializableRiftGeneratable;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

import static net.minecraft.world.level.block.Blocks.AIR;

// https://wiki.fabricmc.net/tutorial:chunkgenerator
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FastRiftGenerator extends ChunkGenerator {

    public static final MapCodec<FastRiftGenerator> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BiomeSource.CODEC.fieldOf("biome_source").forGetter(FastRiftGenerator::getBiomeSource),
            Codec.INT.fieldOf("layer_count").forGetter(FastRiftGenerator::layerCount),
            Codec.INT.fieldOf("height_blocks").forGetter(FastRiftGenerator::getDimensionHeightBlocks),
            RiftConfig.CODEC.fieldOf("rift").forGetter(FastRiftGenerator::getRiftConfig)
    ).apply(instance, FastRiftGenerator::new));

    public static final int MARGIN_LAYERS = 2;

    public static final int SEED_ADJUSTMENT_ROOM_GENERATOR = 949_616_156;
    public static final int SEED_ADJUSTMENT_CORRIDOR_BLENDER = 496_415;

    private final int layerCount;
    private final int dimensionHeightBlocks;
    private final RiftGenerationPerformanceMetrics riftGenerationPerformanceMetrics = new RiftGenerationPerformanceMetrics();
    private final RiftConfig config;
    private final AtomicReference<RiftLayout> layout = new AtomicReference<>();
    private final RiftRoomGenerator roomGenerator;
    private final PositionalRandomFactory roomGeneratorRNG;
    private final SerializableRiftGeneratable filler;

    public FastRiftGenerator(BiomeSource biomeSource, int layerCount, int dimensionHeightBlocks, RiftConfig config) {
        super(biomeSource);
        this.layerCount = layerCount;
        this.dimensionHeightBlocks = dimensionHeightBlocks;
        this.config = config;
        this.filler = config.getCustomData(WotrRiftConfigDataTypes.RIFT_GENERATOR_CONFIG).emptyChunkGeneratable();

        var riftGenerationConfig = this.getRiftGenerationConfig();

        this.roomGeneratorRNG = RandomSourceFromJavaRandom.positional(
                RandomSourceFromJavaRandom.get(RandomFactoryType.DEFAULT),
                config.seed() + SEED_ADJUSTMENT_ROOM_GENERATOR);
        this.roomGenerator = riftGenerationConfig.roomGenerator().create(config);
    }

    @Override
    protected MapCodec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    public RiftConfig getRiftConfig() {
        return config;
    }

    public RiftGenerationConfig getRiftGenerationConfig() {
        return getRiftConfig().getCustomData(WotrRiftConfigDataTypes.RIFT_GENERATOR_CONFIG);
    }

    public RiftLayout getOrCreateLayout(MinecraftServer server) {
        if (layout.get() == null) {
            layout.compareAndSet(null, getRiftGenerationConfig().layout().createLayout(server, config));
        }
        return layout.get();
    }

    public Object2IntMap<RiftGeneratableId> getGeneratableCounts(RoomRiftSpace space, ServerLevelAccessor level) {
        return roomGenerator.getGeneratableCounts(space, level, roomGeneratorRNG);
    }

    private RiftRoomGenerator getRoomGenerator() {
        return roomGenerator;
    }

    @Override
    public void applyBiomeDecoration(WorldGenLevel level, ChunkAccess chunk, StructureManager structureManager) {
        var stepsOptional = getRiftGenerationConfig().postProcessingSteps();
        for (var step : stepsOptional) {
            step.runPostProcessing(this, chunk,
                    RandomSourceFromJavaRandom.positional(RandomSourceFromJavaRandom.get(RandomFactoryType.DEFAULT),
                            this.getRiftConfig().seed() + SEED_ADJUSTMENT_CORRIDOR_BLENDER),
                    level);
        }
        super.applyBiomeDecoration(level, chunk, structureManager);
    }

    @Override
    public void applyCarvers(
            WorldGenRegion level,
            long seed,
            RandomState random,
            BiomeManager biomeManager,
            StructureManager structureManager,
            ChunkAccess chunk) {
    }

    public int layerCount() {
        return layerCount;
    }

    @Override
    public void buildSurface(
            WorldGenRegion level,
            StructureManager structureManager,
            RandomState random,
            ChunkAccess chunk) {
    }

    @Override
    public void spawnOriginalMobs(WorldGenRegion level) {
    }

    @Override
    public int getGenDepth() {
        return dimensionHeightBlocks;
    }

    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(
            Blender blender,
            RandomState randomState,
            StructureManager structureManager,
            ChunkAccess chunk) {
        riftGenerationPerformanceMetrics.chunkStarted();

        return CompletableFuture.supplyAsync(() -> {
            var level = (ServerLevelAccessor) ((AccessorStructureManager) structureManager).getLevel();
            runRiftGeneration(chunk, level);
            return chunk;
        }, Thread::startVirtualThread);
    }

    private void runRiftGeneration(ChunkAccess chunk, ServerLevelAccessor level) {
        Future<RiftProcessedChunk>[] chunkFutures = new Future[layerCount];
        var layout = getOrCreateLayout(level.getServer());
        var roomGenerator = getRoomGenerator();
        for (int i = 0; i < layerCount; i++) {
            var position = new Vec3i(chunk.getPos().x, i - layerCount / 2, chunk.getPos().z);
            var space = layout.getChunkSpace(position);
            if (space instanceof RoomRiftSpace roomSpace) {
                chunkFutures[i] = roomGenerator.getAndRemoveRoomChunk(position, roomSpace, level, roomGeneratorRNG);
            } else {
                chunkFutures[i] = RiftRoomGenerator.chunkOf(filler, level, position);
            }
        }
        for (Future<RiftProcessedChunk> generatedRoomChunkFuture : chunkFutures) {
            try {
                RiftProcessedChunk generatedRoomChunk = generatedRoomChunkFuture.get();
                if (generatedRoomChunk != null) {
                    generatedRoomChunk.placeInWorld(chunk, level);
                }
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }

        }
        riftGenerationPerformanceMetrics.chunkEnded();
    }

    @Override
    public int getSeaLevel() {
        return -dimensionHeightBlocks / 2;
    }

    @Override
    public int getMinY() {
        return -dimensionHeightBlocks / 2;
    }

    @Override
    public int getBaseHeight(int x, int z, Heightmap.Types type, LevelHeightAccessor level, RandomState random) {
        return layerCount << RiftProcessedChunk.CHUNK_HEIGHT_SHIFT;
    }

    @Override
    public @NotNull NoiseColumn getBaseColumn(int x, int z, LevelHeightAccessor height, RandomState random) {
        return new NoiseColumn(0, new BlockState[] { AIR.defaultBlockState() });
    }

    @Override
    public void addDebugScreenInfo(List<String> info, RandomState random, BlockPos pos) {
        var layout = this.layout.get();
        if (layout != null) {
            var currentSpace = layout.getChunkSpace(SectionPos.of(pos));
            info.add("current space");
            if (currentSpace == null || currentSpace instanceof VoidRiftSpace) {
                info.add("void");
            } else {
                info.add(MessageFormat.format("origin: {0} {1} {2}", currentSpace.origin().getX(),
                        currentSpace.origin().getY(), currentSpace.origin().getZ()));
                info.add(MessageFormat.format("size: {0} {1} {2}", currentSpace.size().getX(),
                        currentSpace.size().getY(), currentSpace.size().getZ()));
                info.add(MessageFormat.format("transform: {0} {1} {2}", currentSpace.templateTransform().x(),
                        currentSpace.templateTransform().z(), currentSpace.templateTransform().diagonal()));
                var template = currentSpace.template();
                if (template != null) {
                    info.add("room base template: " + template.identifier());
                }
            }
        }
        riftGenerationPerformanceMetrics.addDebugScreenInfo(info);
    }

    public int getDimensionHeightBlocks() {
        return dimensionHeightBlocks;
    }
}
