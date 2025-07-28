package com.wanderersoftherift.wotr.world.level;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.item.riftkey.RiftConfig;
import com.wanderersoftherift.wotr.item.riftkey.RiftGenerationConfig;
import com.wanderersoftherift.wotr.mixin.AccessorStructureManager;
import com.wanderersoftherift.wotr.util.RandomSourceFromJavaRandom;
import com.wanderersoftherift.wotr.world.level.levelgen.RiftProcessedChunk;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.RiftLayout;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.util.ProcessorUtil;
import com.wanderersoftherift.wotr.world.level.levelgen.roomgen.RiftRoomGenerator;
import com.wanderersoftherift.wotr.world.level.levelgen.space.RoomRiftSpace;
import com.wanderersoftherift.wotr.world.level.levelgen.space.VoidRiftSpace;
import com.wanderersoftherift.wotr.world.level.levelgen.template.RiftGeneratable;
import com.wanderersoftherift.wotr.world.level.levelgen.template.SingleBlockChunkGeneratable;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Block;
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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import static net.minecraft.core.Direction.NORTH;
import static net.minecraft.core.Direction.WEST;
import static net.minecraft.world.level.block.Blocks.AIR;

// https://wiki.fabricmc.net/tutorial:chunkgenerator
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FastRiftGenerator extends ChunkGenerator {

    public static final MapCodec<FastRiftGenerator> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BiomeSource.CODEC.fieldOf("biome_source").forGetter(FastRiftGenerator::getBiomeSource),
            Codec.INT.fieldOf("layer_count").forGetter(FastRiftGenerator::layersCount),
            Codec.INT.fieldOf("height_blocks").forGetter(FastRiftGenerator::getDimensionHeightBlocks),
            ResourceLocation.CODEC.fieldOf("custom_block").forGetter(FastRiftGenerator::getCustomBlockID),
            RiftConfig.CODEC.fieldOf("rift").forGetter(FastRiftGenerator::getRiftConfig)
    ).apply(instance, FastRiftGenerator::new));
    public static final int MARGIN_LAYERS = 2;

    public static final int CORRIDOR_WIDTH = 5;
    public static final int CORRIDOR_HEIGHT = 7;
    public static final int CORRIDOR_START_X = 6;
    public static final int CORRIDOR_START_Y = 5;
    public static final int CORRIDOR_OPTIONAL_START_X = 1; // optionals are in corridor space
    public static final int CORRIDOR_OPTIONAL_START_Y = 2;
    public static final int CORRIDOR_OPTIONAL_END_X = 3;
    public static final int CORRIDOR_OPTIONAL_END_Y = 4;

    public static final int SEED_ADJUSTMENT_ROOM_GENERATOR = 949_616_156;
    public static final int SEED_ADJUSTMENT_CORRIDOR_BLENDER = 496_415;

    public static final int MAX_MEASUREMENT_PAUSE_MILLISECONDS = 3000;

    private final int layerCount;

    private final ResourceLocation customBlockID;
    private final int dimensionHeightBlocks;
    private final BlockState customBlock;
    private final AtomicInteger inFlightChunks = new AtomicInteger(0);

    private final AtomicInteger completedChunks = new AtomicInteger(0);
    private final AtomicInteger completedChunksInWindow = new AtomicInteger(0);
    private final AtomicLong lastChunkStart = new AtomicLong(0);
    private long generationStart = 0;
    private final RiftConfig config;
    private final AtomicReference<RiftLayout> layout = new AtomicReference<>();
    private final RiftRoomGenerator roomGenerator;
    private final PositionalRandomFactory roomGeneratorRNG;
    private RiftGeneratable filler;

    public FastRiftGenerator(BiomeSource biomeSource, int layerCount, int dimensionHeightBlocks,
            ResourceLocation defaultBlock, RiftConfig config) {
        super(biomeSource);
        this.layerCount = layerCount;
        this.dimensionHeightBlocks = dimensionHeightBlocks;
        this.customBlock = BuiltInRegistries.BLOCK.get(defaultBlock)
                .map(Holder.Reference::value)
                .map(Block::defaultBlockState)
                .orElse(AIR.defaultBlockState());
        this.customBlockID = defaultBlock;
        this.config = config;
        this.filler = new SingleBlockChunkGeneratable(customBlock);

        this.roomGeneratorRNG = RandomSourceFromJavaRandom.positional(RandomSourceFromJavaRandom.get(0),
                this.getRiftGenerationConfig().seed().orElse(0) + SEED_ADJUSTMENT_ROOM_GENERATOR);
        this.roomGenerator = this.getRiftGenerationConfig().roomGenerator().get().create(config);
    }

    @Override
    protected MapCodec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    public RiftConfig getRiftConfig() {
        return config;
    }

    public RiftGenerationConfig getRiftGenerationConfig() {
        return getRiftConfig().riftGen();
    }

    public RiftLayout getOrCreateLayout(MinecraftServer server) {
        if (layout.get() == null) {
            layout.compareAndSet(null,
                    getRiftGenerationConfig().layout()
                            .get()
                            .createLayout(server, getRiftGenerationConfig().seed().get(), config));
        }
        return layout.get();
    }

    private RiftRoomGenerator getRoomGenerator() {
        return roomGenerator;
    }

    @Override
    public void applyBiomeDecoration(WorldGenLevel level, ChunkAccess chunk, StructureManager structureManager) {
        runCorridorBlender(chunk, RandomSourceFromJavaRandom.positional(RandomSourceFromJavaRandom.get(0),
                this.getRiftGenerationConfig().seed().orElse(0) + SEED_ADJUSTMENT_CORRIDOR_BLENDER), level);
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

    private int layersCount() {
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
        var time = System.currentTimeMillis();
        if (inFlightChunks.getAndIncrement() == 0 && time - lastChunkStart.get() > MAX_MEASUREMENT_PAUSE_MILLISECONDS) {
            generationStart = time;
            completedChunksInWindow.set(0);
        }

        lastChunkStart.updateAndGet((value) -> Math.max(value, time));

        return CompletableFuture.supplyAsync(() -> {
            var level = (ServerLevelAccessor) ((AccessorStructureManager) structureManager).getLevel();
            runRiftGeneration(chunk, level);
            return chunk;
        }, Thread::startVirtualThread);
    }

    private void runRiftGeneration(ChunkAccess chunk, ServerLevelAccessor level) {

        if (false) { // for testing how quick is generation of empty world
            inFlightChunks.decrementAndGet();
            completedChunks.incrementAndGet();
            completedChunksInWindow.incrementAndGet();
            return;
        }
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
        inFlightChunks.decrementAndGet();
        completedChunks.incrementAndGet();
        completedChunksInWindow.incrementAndGet();
    }

    private void runCorridorBlender(ChunkAccess chunk, PositionalRandomFactory randomFactory, WorldGenLevel level) {
        if (!getRiftGenerationConfig().generatePassages()) {
            return;
        }
        var rng = randomFactory.at(chunk.getPos().x, 0, chunk.getPos().z);
        var layout = getOrCreateLayout(level.getServer());
        for (int i = 0; i < layerCount; i++) {
            var chunkX = chunk.getPos().x;
            var chunkY = i - layerCount / 2;
            var chunkZ = chunk.getPos().z;
            runCorridorBlenderDirectional(layout, chunkX, chunkY, chunkZ, NORTH, level, rng);
            runCorridorBlenderDirectional(layout, chunkX, chunkY, chunkZ, WEST, level, rng);
        }
    }

    private void runCorridorBlenderDirectional(
            RiftLayout layout,
            int chunkX,
            int chunkY,
            int chunkZ,
            Direction direction,
            WorldGenLevel level,
            RandomSource rng) {
        if (layout.validateCorridor(chunkX, chunkY, chunkZ, direction)) {
            for (int x = 0; x < CORRIDOR_WIDTH; x++) {
                for (int y = 0; y < CORRIDOR_HEIGHT; y++) {
                    var pos = new BlockPos(
                            (chunkX << RiftProcessedChunk.CHUNK_WIDTH_SHIFT)
                                    - (x + CORRIDOR_START_X) * direction.getStepZ(),
                            (chunkY << RiftProcessedChunk.CHUNK_HEIGHT_SHIFT) + CORRIDOR_START_Y + y,
                            (chunkZ << RiftProcessedChunk.CHUNK_WIDTH_SHIFT)
                                    - (x + CORRIDOR_START_X) * direction.getStepX());
                    var posOffset = pos.relative(direction);
                    var state = level.getBlockState(pos.relative(direction.getOpposite()));
                    if (x >= CORRIDOR_OPTIONAL_START_X && x <= CORRIDOR_OPTIONAL_END_X && y >= CORRIDOR_OPTIONAL_START_Y
                            && y <= CORRIDOR_OPTIONAL_END_Y) {
                        if (rng.nextBoolean()) {
                            state = level.getBlockState(posOffset);
                        }
                    } else {
                        if (rng.nextBoolean() || !isStateAllowedByCorridorBlender(state, direction)) {
                            var newState = level.getBlockState(posOffset);
                            if (isStateAllowedByCorridorBlender(newState, direction)) {
                                state = level.getBlockState(posOffset);
                            }
                        }
                    }
                    if (!isStateAllowedByCorridorBlender(state, direction)) {
                        state = AIR.defaultBlockState();
                    }
                    level.setBlock(pos, state, 0);
                }
            }
        }
    }

    private boolean isStateAllowedByCorridorBlender(BlockState state, Direction direction) {
        if (!state.canOcclude()) {
            return false;
        }
        var shape = state.getOcclusionShape();
        return ProcessorUtil.isFaceFullFast(shape, direction) == ProcessorUtil.isFaceFullFast(shape,
                direction.getOpposite());
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
        info.add("performance");
        info.add("all generated chunks: " + completedChunks.get());
        info.add("window chunks: " + completedChunksInWindow.get());
        info.add("window time: " + (lastChunkStart.get() - generationStart));
        info.add("window CPS: " + (completedChunksInWindow.get() * 1000.0 / (lastChunkStart.get() - generationStart)));
        info.add("currently generating chunks: " + inFlightChunks.get());
    }

    public ResourceLocation getCustomBlockID() {
        return customBlockID;
    }

    public int getDimensionHeightBlocks() {
        return dimensionHeightBlocks;
    }
}
