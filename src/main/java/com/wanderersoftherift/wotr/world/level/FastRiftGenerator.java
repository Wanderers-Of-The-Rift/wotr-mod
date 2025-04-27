package com.wanderersoftherift.wotr.world.level;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.mixin.AccessorStructureManager;
import com.wanderersoftherift.wotr.world.level.levelgen.RiftProcessedChunk;
import com.wanderersoftherift.wotr.world.level.levelgen.RiftRoomGenerator;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.ChaoticLayoutRegion;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.ChaoticRiftLayout;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.RiftLayout;
import com.wanderersoftherift.wotr.world.level.levelgen.space.RiftSpace;
import com.wanderersoftherift.wotr.world.level.levelgen.space.RoomRiftSpace;
import com.wanderersoftherift.wotr.world.level.levelgen.space.VoidRiftSpace;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
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

// https://wiki.fabricmc.net/tutorial:chunkgenerator
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FastRiftGenerator extends ChunkGenerator {

    public static final MapCodec<FastRiftGenerator> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
            BiomeSource.CODEC.fieldOf("biome_source").forGetter(FastRiftGenerator::getBiomeSource),
            ResourceLocation.CODEC.fieldOf("custom_block").forGetter(FastRiftGenerator::getCustomBlockID)
                ).apply(instance, FastRiftGenerator::new));

    private final ResourceLocation customBlockID;
    private final BlockState customBlock;

    private final AtomicInteger inFlightChunks = new AtomicInteger(0);
    private final AtomicInteger completedChunks = new AtomicInteger(0);
    private final AtomicInteger completedChunksInWindow = new AtomicInteger(0);
    private long generationStart = 0;
    private AtomicLong lastChunkStart = new AtomicLong(0);

    private RiftLayout layout = null;
    private RiftRoomGenerator roomGenerator = null;

    public FastRiftGenerator(BiomeSource biomeSource, ResourceLocation defaultBlock) {
        super(biomeSource);
        this.customBlock = BuiltInRegistries.BLOCK.get(defaultBlock).map(Holder.Reference::value).map(Block::defaultBlockState).orElse(AIR.defaultBlockState());
        this.customBlockID = defaultBlock;
    }

    @Override protected MapCodec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    @Override
    public void applyCarvers(WorldGenRegion level, long seed, RandomState random, BiomeManager biomeManager, StructureManager structureManager, ChunkAccess chunk) {

    }

    @Override public void buildSurface(WorldGenRegion level, StructureManager structureManager, RandomState random, ChunkAccess chunk) {

    }

    @Override public void spawnOriginalMobs(WorldGenRegion level) {

    }

    @Override public int getGenDepth() {
        return ChaoticLayoutRegion.LAYERS*16;
    }

    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Blender blender, RandomState randomState, StructureManager structureManager, ChunkAccess chunk) {


        var time = System.currentTimeMillis();
        if (inFlightChunks.getAndIncrement()==0 && time-lastChunkStart.get()>3000) {
            generationStart = time;
            completedChunksInWindow.set(0);
        }

        lastChunkStart.updateAndGet((value)-> Math.max(value, time));
        var level = ((AccessorStructureManager)structureManager).getLevel();
        ServerLevel serverLevel;
        if(level instanceof WorldGenRegion r){
            serverLevel=r.getLevel();
        } else if(level instanceof ServerLevel r){
            serverLevel = r;
        } else throw new IllegalStateException("trying to generate in non-server world");


        runRiftGeneration(chunk,randomState,serverLevel,level);

        return CompletableFuture.completedFuture(chunk);
    }

    private void runRiftGeneration(ChunkAccess chunk, RandomState randomState, ServerLevel serverLevel, LevelAccessor level){

        var threads = new ArrayList<Thread>();
        if(false) {
            inFlightChunks.decrementAndGet();
            completedChunks.incrementAndGet();
            completedChunksInWindow.incrementAndGet();
            return;
        }
        if (layout==null || roomGenerator==null) {
            layout = new ChaoticRiftLayout();
            roomGenerator = new RiftRoomGenerator();
        }
        var spaces = layout.getChunkSpaces(chunk.getPos(),randomState);
        RiftSpace.placeInChunk(chunk,null,spaces.size()- ChaoticLayoutRegion.LAYERS/2);
        RiftSpace.placeInChunk(chunk,null,-1-ChaoticLayoutRegion.LAYERS/2);
        Future<RiftProcessedChunk>[] chunkFutures = new Future[spaces.size()];
        for (int i = 0; i < spaces.size(); i++) {
            var space = spaces.get(i);
            if (space instanceof RoomRiftSpace roomSpace) {
                chunkFutures[i] = roomGenerator.getAndRemoveRoomChunk(new Vec3i(chunk.getPos().x, i-ChaoticLayoutRegion.LAYERS/2, chunk.getPos().z), roomSpace, serverLevel, randomState);
            }
        }
        for (int i = 0; i < spaces.size(); i++) {
            var generatedRoomChunkFuture = chunkFutures[i];

            var space = spaces.get(i);
            if(space == null || space instanceof VoidRiftSpace){
                RiftSpace.placeInChunk(chunk,space,i-ChaoticLayoutRegion.LAYERS/2);
            }else if(generatedRoomChunkFuture!=null) {
                try {
                    RiftProcessedChunk generatedRoomChunk = generatedRoomChunkFuture.get();

                    if(generatedRoomChunk!=null) {
                        threads.add(Thread.startVirtualThread(()-> {
                            RiftSpace.placeInRiftChunk(generatedRoomChunk, space);
                            generatedRoomChunk.placeInWorld(chunk, level);
                        }));
                    }else {
                        RiftSpace.placeInChunk(chunk,space,i-ChaoticLayoutRegion.LAYERS/2);
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
        return -ChaoticLayoutRegion.LAYERS*8;
    }

    @Override public int getMinY() {
        return -ChaoticLayoutRegion.LAYERS*8;
    }

    @Override public int getBaseHeight(int x, int z, Heightmap.Types type, LevelHeightAccessor level, RandomState random) {
        return ChaoticLayoutRegion.LAYERS*16;
    }

    @Override public @NotNull NoiseColumn getBaseColumn(int x, int z, LevelHeightAccessor height, RandomState random) {
        return new NoiseColumn(0, new BlockState[]{AIR.defaultBlockState()});
    }

    @Override public void addDebugScreenInfo(List<String> info, RandomState random, BlockPos pos) {//todo more debug info: used template and placement, performance, etc.
        if (layout!=null){
            var currentSpace = ((pos.getY()+8*ChaoticLayoutRegion.LAYERS)>=0 && (pos.getY()+8*ChaoticLayoutRegion.LAYERS)<16*ChaoticLayoutRegion.LAYERS)?layout.getChunkSpaces(new ChunkPos(pos), null).get((pos.getY()+ChaoticLayoutRegion.LAYERS*8)/16):null;
            info.add("current space");
            if(currentSpace==null || currentSpace instanceof VoidRiftSpace){
                info.add("void");
            } else {
                info.add("origin: " + currentSpace.origin().getX() + " " + currentSpace.origin().getY() + " " + currentSpace.origin().getZ());
                info.add("size: " + currentSpace.size().getX() + " " + currentSpace.size().getY() + " " + currentSpace.size().getZ());
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
