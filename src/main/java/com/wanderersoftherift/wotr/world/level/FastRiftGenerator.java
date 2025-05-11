package com.wanderersoftherift.wotr.world.level;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.mixin.AccessorStructureManager;
import com.wanderersoftherift.wotr.world.level.levelgen.RiftProcessedChunk;
import com.wanderersoftherift.wotr.world.level.levelgen.RiftRoomGenerator;
import com.wanderersoftherift.wotr.world.level.levelgen.RoomRandomizerImpl;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.ChaoticRiftLayout;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.RiftLayout;
import com.wanderersoftherift.wotr.world.level.levelgen.space.RiftSpace;
import com.wanderersoftherift.wotr.world.level.levelgen.space.RoomRiftSpace;
import com.wanderersoftherift.wotr.world.level.levelgen.space.VoidRiftSpace;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.*;
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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static net.minecraft.world.level.block.Blocks.AIR;

/*
 * todo rift tier
 */

// https://wiki.fabricmc.net/tutorial:chunkgenerator
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FastRiftGenerator extends ChunkGenerator {

    public static final MapCodec<FastRiftGenerator> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
            BiomeSource.CODEC.fieldOf("biome_source").forGetter(FastRiftGenerator::getBiomeSource),
                Codec.INT.fieldOf("layer_count").forGetter(FastRiftGenerator::layersCount),
            ResourceLocation.CODEC.fieldOf("custom_block").forGetter(FastRiftGenerator::getCustomBlockID)
                ).apply(instance, FastRiftGenerator::new));

    private final int layerCount;

    private final ResourceLocation customBlockID;
    private final BlockState customBlock;

    private final AtomicInteger inFlightChunks = new AtomicInteger(0);
    private final AtomicInteger completedChunks = new AtomicInteger(0);
    private final AtomicInteger completedChunksInWindow = new AtomicInteger(0);
    private long generationStart = 0;
    private final AtomicLong lastChunkStart = new AtomicLong(0);
    public RiftLayout layout = null;
    public RiftRoomGenerator roomGenerator = null;

    public FastRiftGenerator(BiomeSource biomeSource, int layerCount, ResourceLocation defaultBlock) {
        super(biomeSource);
        this.layerCount = layerCount;
        this.customBlock = BuiltInRegistries.BLOCK.get(defaultBlock).map(Holder.Reference::value).map(Block::defaultBlockState).orElse(AIR.defaultBlockState());
        this.customBlockID = defaultBlock;
    }

    @Override protected MapCodec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    @Override
    public void applyCarvers(WorldGenRegion level, long seed, RandomState random, BiomeManager biomeManager, StructureManager structureManager, ChunkAccess chunk) {

    }

    private int layersCount() {
        return layerCount;
    }

    @Override public void buildSurface(WorldGenRegion level, StructureManager structureManager, RandomState random, ChunkAccess chunk) {

    }

    @Override public void spawnOriginalMobs(WorldGenRegion level) {

    }

    @Override public int getGenDepth() {
        return layerCount *16;
    }

    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Blender blender, RandomState randomState, StructureManager structureManager, ChunkAccess chunk) {
        var time = System.currentTimeMillis();
        if (inFlightChunks.getAndIncrement()==0 && time-lastChunkStart.get()>3000) {
            generationStart = time;
            completedChunksInWindow.set(0);
        }

        lastChunkStart.updateAndGet((value)-> Math.max(value, time));
        var level = (ServerLevelAccessor) ((AccessorStructureManager)structureManager).getLevel();

        runRiftGeneration(chunk, randomState, level);

        return CompletableFuture.completedFuture(chunk);
    }

    private void runRiftGeneration(ChunkAccess chunk, RandomState randomState,  ServerLevelAccessor level){

        var threads = new ArrayList<Thread>();
        if(false) { //for testing how quick is generation of empty world
            inFlightChunks.decrementAndGet();
            completedChunks.incrementAndGet();
            completedChunksInWindow.incrementAndGet();
            return;
        }
        if (layout==null || roomGenerator==null) {
            layout = new ChaoticRiftLayout(layerCount-2, new RoomRandomizerImpl(level.getServer()));
            roomGenerator = new RiftRoomGenerator();
        }
        var perimeterBlock = customBlock;
        RiftSpace.placePerimeterInChunk(chunk, null, -1 + layerCount/2, perimeterBlock);
        RiftSpace.placePerimeterInChunk(chunk, null, -layerCount/2, perimeterBlock);
        Future<RiftProcessedChunk>[] chunkFutures = new Future[layerCount - 2];
        RiftSpace[] spaces = new RiftSpace[layerCount - 2];
        for (int i = 0; i < layerCount - 2; i++) {
            var position = new Vec3i(chunk.getPos().x, 1 + i - layerCount/2, chunk.getPos().z);
            var space = layout.getChunkSpace(position, randomState);
            if (space instanceof RoomRiftSpace roomSpace) {
                chunkFutures[i] = roomGenerator.getAndRemoveRoomChunk(position, roomSpace, level, randomState);
                spaces[i] = space;
            }
        }
        for (int i = 0; i < chunkFutures.length; i++) {
            var generatedRoomChunkFuture = chunkFutures[i];

            var space = spaces[i];
            if(space == null || space instanceof VoidRiftSpace){
                RiftSpace.placePerimeterInChunk(chunk, space, 1 + i - layerCount/2, perimeterBlock);
            }else if(generatedRoomChunkFuture != null) {
                try {
                    RiftProcessedChunk generatedRoomChunk = generatedRoomChunkFuture.get();

                    if(generatedRoomChunk!=null) {
                        threads.add(Thread.startVirtualThread(()-> {
                            RiftSpace.placePerimeterInRiftChunk(generatedRoomChunk, space, perimeterBlock);
                            generatedRoomChunk.placeInWorld(chunk, level);
                        }));
                    }else {
                        RiftSpace.placePerimeterInChunk(chunk, space, 1 + i - layerCount/2, perimeterBlock);
                    }
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }

        }

        for (var thread:threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        inFlightChunks.decrementAndGet();
        completedChunks.incrementAndGet();
        completedChunksInWindow.incrementAndGet();
    }

    @Override public int getSeaLevel() {
        return -layerCount *8;
    }

    @Override public int getMinY() {
        return -layerCount *8;
    }

    @Override public int getBaseHeight(int x, int z, Heightmap.Types type, LevelHeightAccessor level, RandomState random) {
        return layerCount *16;
    }

    @Override public @NotNull NoiseColumn getBaseColumn(int x, int z, LevelHeightAccessor height, RandomState random) {
        return new NoiseColumn(0, new BlockState[]{AIR.defaultBlockState()});
    }

    @Override public void addDebugScreenInfo(List<String> info, RandomState random, BlockPos pos) {
        if (layout!=null){
            var currentSpace = layout.getChunkSpace(SectionPos.of(pos), null);
            info.add("current space");
            if(currentSpace==null || currentSpace instanceof VoidRiftSpace){
                info.add("void");
            } else {
                info.add("origin: " + currentSpace.origin().getX() + " " + currentSpace.origin().getY() + " " + currentSpace.origin().getZ());
                info.add("size: " + currentSpace.size().getX() + " " + currentSpace.size().getY() + " " + currentSpace.size().getZ());
                info.add("transform: " + currentSpace.templateTransform().x() + " " + currentSpace.templateTransform().z() + " " + currentSpace.templateTransform().diagonal());
                var template = currentSpace.template();
                if(template!=null) info.add("room base template: " + template.identifier());
            }
        }
        info.add("performance");
        info.add("all generated chunks: "+completedChunks.get());
        info.add("window chunks: "+completedChunksInWindow.get());
        info.add("window time: "+(lastChunkStart.get()-generationStart));
        info.add("window CPS: "+(completedChunksInWindow.get()*1000.0/(lastChunkStart.get()-generationStart)));
        info.add("currently generating chunks: "+inFlightChunks.get());
    }
    public ResourceLocation getCustomBlockID() {
        return customBlockID;
    }
}
