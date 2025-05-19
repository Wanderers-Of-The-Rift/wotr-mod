package com.wanderersoftherift.wotr.world.level;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.item.riftkey.RiftConfig;
import com.wanderersoftherift.wotr.mixin.AccessorStructureManager;
import com.wanderersoftherift.wotr.world.level.levelgen.RiftProcessedChunk;
import com.wanderersoftherift.wotr.world.level.levelgen.RiftRoomGenerator;
import com.wanderersoftherift.wotr.world.level.levelgen.RoomRandomizerImpl;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.ChaoticRiftLayout;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.RiftLayout;
import com.wanderersoftherift.wotr.world.level.levelgen.space.RoomRiftSpace;
import com.wanderersoftherift.wotr.world.level.levelgen.space.VoidRiftSpace;
import com.wanderersoftherift.wotr.world.level.levelgen.template.PerimeterGeneratable;
import com.wanderersoftherift.wotr.world.level.levelgen.template.RiftGeneratable;
import com.wanderersoftherift.wotr.world.level.levelgen.template.SingleBlockChunkGeneratable;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
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

import static net.minecraft.world.level.block.Blocks.AIR;

/*
 * todo use rift tier
 */

// https://wiki.fabricmc.net/tutorial:chunkgenerator
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FastRiftGenerator extends ChunkGenerator {

    public static final MapCodec<FastRiftGenerator> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BiomeSource.CODEC.fieldOf("biome_source").forGetter(FastRiftGenerator::getBiomeSource),
            Codec.INT.fieldOf("layer_count").forGetter(FastRiftGenerator::layersCount),
            ResourceLocation.CODEC.fieldOf("custom_block").forGetter(FastRiftGenerator::getCustomBlockID),
            RiftConfig.CODEC.fieldOf("rift").forGetter(FastRiftGenerator::getRiftConfig)
    ).apply(instance, FastRiftGenerator::new));

    private final int layerCount;

    private final ResourceLocation customBlockID;
    private final BlockState customBlock;
    private final AtomicInteger inFlightChunks = new AtomicInteger(0);

    private final AtomicInteger completedChunks = new AtomicInteger(0);
    private final AtomicInteger completedChunksInWindow = new AtomicInteger(0);
    private final AtomicLong lastChunkStart = new AtomicLong(0);
    private long generationStart = 0;
    private final RiftConfig config;
    private AtomicReference<RiftLayout> layout = new AtomicReference<>();
    private AtomicReference<RiftRoomGenerator> roomGenerator = new AtomicReference<>();
    private RiftGeneratable filler;
    private RiftGeneratable perimeter;

    public FastRiftGenerator(BiomeSource biomeSource, int layerCount, ResourceLocation defaultBlock,
            RiftConfig config) {
        super(biomeSource);
        this.layerCount = layerCount;
        this.customBlock = BuiltInRegistries.BLOCK.get(defaultBlock)
                .map(Holder.Reference::value)
                .map(Block::defaultBlockState)
                .orElse(AIR.defaultBlockState());
        this.customBlockID = defaultBlock;
        this.config = config;
        filler = new SingleBlockChunkGeneratable(customBlock);
    }

    @Override
    protected MapCodec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    public RiftConfig getRiftConfig() {
        return config;
    }

    public RiftLayout getOrCreateLayout(MinecraftServer server) {
        if (layout.get() == null) {
            layout.compareAndSet(null,
                    new ChaoticRiftLayout(layerCount - 2, config.seed().orElseThrow(), new RoomRandomizerImpl(server)));
            perimeter = new PerimeterGeneratable(customBlock, layout.get());
        }
        return layout.get();
    }

    private RiftRoomGenerator getOrCreateRoomGenerator() {
        if (roomGenerator.get() == null) {
            roomGenerator.compareAndSet(null, new RiftRoomGenerator());
        }
        return roomGenerator.get();
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
        return layerCount * 16;
    }

    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(
            Blender blender,
            RandomState randomState,
            StructureManager structureManager,
            ChunkAccess chunk) {
        var time = System.currentTimeMillis();
        if (inFlightChunks.getAndIncrement() == 0 && time - lastChunkStart.get() > 3000) {
            generationStart = time;
            completedChunksInWindow.set(0);
        }

        lastChunkStart.updateAndGet((value) -> Math.max(value, time));

        return CompletableFuture.supplyAsync(() -> {
            var level = (ServerLevelAccessor) ((AccessorStructureManager) structureManager).getLevel();
            runRiftGeneration(chunk, randomState, level);
            return chunk;
        }, Thread::startVirtualThread);
    }

    private void runRiftGeneration(ChunkAccess chunk, RandomState randomState, ServerLevelAccessor level) {

        if (false) { // for testing how quick is generation of empty world
            inFlightChunks.decrementAndGet();
            completedChunks.incrementAndGet();
            completedChunksInWindow.incrementAndGet();
            return;
        }
        Future<RiftProcessedChunk>[] chunkFutures = new Future[layerCount];
        var layout = getOrCreateLayout(level.getServer());
        var roomGenerator = getOrCreateRoomGenerator();
        for (int i = 0; i < layerCount; i++) {
            var position = new Vec3i(chunk.getPos().x, i - layerCount / 2, chunk.getPos().z);
            var space = layout.getChunkSpace(position);
            if (space instanceof RoomRiftSpace roomSpace) {
                chunkFutures[i] = roomGenerator.getAndRemoveRoomChunk(position, roomSpace, level, randomState,
                        perimeter);
            } else {
                chunkFutures[i] = roomGenerator.chunkOf(filler, level, position);
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

    @Override
    public int getSeaLevel() {
        return -layerCount * 8;
    }

    @Override
    public int getMinY() {
        return -layerCount * 8;
    }

    @Override
    public int getBaseHeight(int x, int z, Heightmap.Types type, LevelHeightAccessor level, RandomState random) {
        return layerCount * 16;
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
}
