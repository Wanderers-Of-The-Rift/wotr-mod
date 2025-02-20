package com.wanderersoftherift.wotr.world.level;

import com.google.common.collect.Sets;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static net.minecraft.world.level.block.Blocks.AIR;

// https://wiki.fabricmc.net/tutorial:chunkgenerator
public class PocRiftChunkGenerator extends ChunkGenerator {

    public static final MapCodec<PocRiftChunkGenerator> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
                ResourceLocation.CODEC.fieldOf("custom_block").forGetter(PocRiftChunkGenerator::getCustomBlockID)
                ).apply(instance, PocRiftChunkGenerator::new));

    private final ResourceLocation customBlockID;
    private final BlockState customBlock;

    public PocRiftChunkGenerator(ResourceLocation defaultBlock) {
        super(new FixedBiomeSource(ServerLifecycleHooks.getCurrentServer().overworld().registryAccess().lookupOrThrow(Registries.BIOME).get(Biomes.THE_VOID).get()));
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
        return 384;
    }

    @Override public CompletableFuture<ChunkAccess> fillFromNoise(Blender blender, RandomState randomState, StructureManager structureManager, ChunkAccess chunk) {
        int i = getMinY();
        int j = Mth.floorDiv(i, getGenDepth());
        int k = Mth.floorDiv(getGenDepth(), getGenDepth());
        return CompletableFuture.supplyAsync(() -> {
            int l = chunk.getSectionIndex(k * getGenDepth() - 1 + i);
            int i1 = chunk.getSectionIndex(i);
            Set<LevelChunkSection> set = Sets.newHashSet();

            for (int j1 = l; j1 >= i1; j1--) {
                LevelChunkSection levelchunksection = chunk.getSection(j1);
                levelchunksection.acquire();
                set.add(levelchunksection);
            }

            ChunkAccess chunkaccess;
            try {
                chunkaccess = this.doFill(chunk, j, k);
            } finally {
                for (LevelChunkSection levelchunksection1 : set) {
                    levelchunksection1.release();
                }
            }

            return chunkaccess;
        }, Util.backgroundExecutor().forName("wgen_fill_noise"));
    }

    private ChunkAccess doFill(ChunkAccess chunk, int minCellY, int cellCountY) { // just copied from the vanilla noisegen
        Heightmap heightmap1 = chunk.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE_WG);
        ChunkPos chunkpos = chunk.getPos();
        int i = chunkpos.getMinBlockX();
        int j = chunkpos.getMinBlockZ();
        int k = 16; // width
        int l = 64; // height
        int i1 = 16 / k;
        int j1 = 16 / k;

        for (int k1 = 0; k1 < i1; k1++) {

            for (int l1 = 0; l1 < j1; l1++) {
                int i2 = chunk.getSectionsCount() - 1;
                LevelChunkSection levelchunksection = chunk.getSection(i2);

                for (int j2 = cellCountY - 1; j2 >= 0; j2--) {

                    for (int k2 = l - 1; k2 >= 0; k2--) {
                        int l2 = (minCellY + j2) * l + k2;
                        int i3 = l2 & 15;
                        int j3 = chunk.getSectionIndex(l2);
                        if (i2 != j3) {
                            i2 = j3;
                            levelchunksection = chunk.getSection(j3);
                        }


                        for (int k3 = 0; k3 < k; k3++) {
                            int l3 = i + k1 * k + k3;
                            int i4 = l3 & 15;

                            for (int j4 = 0; j4 < k; j4++) {
                                int k4 = j + l1 * k + j4;
                                int l4 = k4 & 15;
                                BlockState blockstate = this.customBlock;

                                if (blockstate != AIR.defaultBlockState() && !SharedConstants.debugVoidTerrain(chunk.getPos())) {
                                    levelchunksection.setBlockState(i4, i3, l4, blockstate, false);
                                    heightmap1.update(i4, l2, l4, blockstate);
                                }
                            }
                        }
                    }
                }
            }

        }

        return chunk;
    }

    @Override public int getSeaLevel() {
        return -64;
    }

    @Override public int getMinY() {
        return -64;
    }

    @Override public int getBaseHeight(int x, int z, Heightmap.Types type, LevelHeightAccessor level, RandomState random) {
        return 384;
    }

    @Override public @NotNull NoiseColumn getBaseColumn(int x, int z, LevelHeightAccessor height, RandomState random) {
        return new NoiseColumn(0, new BlockState[]{AIR.defaultBlockState()});
    }

    @Override public void addDebugScreenInfo(List<String> info, RandomState random, BlockPos pos) {
        info.add("MelGen");
    }
    public ResourceLocation getCustomBlockID() {
        return customBlockID;
    }
}
