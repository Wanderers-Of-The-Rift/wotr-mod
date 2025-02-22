package com.wanderersoftherift.wotr.world.level;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static net.minecraft.world.level.block.Blocks.AIR;

// https://wiki.fabricmc.net/tutorial:chunkgenerator
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class PocRiftChunkGenerator extends ChunkGenerator {

    public static final MapCodec<PocRiftChunkGenerator> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
            BiomeSource.CODEC.fieldOf("biome_source").forGetter(PocRiftChunkGenerator::getBiomeSource),
            ResourceLocation.CODEC.fieldOf("custom_block").forGetter(PocRiftChunkGenerator::getCustomBlockID)
                ).apply(instance, PocRiftChunkGenerator::new));

    private final ResourceLocation customBlockID;
    private final BlockState customBlock;

    public PocRiftChunkGenerator(BiomeSource biomeSource, ResourceLocation defaultBlock) {
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
        return 384;
    }

    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Blender blender, RandomState randomState, StructureManager structureManager, ChunkAccess chunk) {
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
        Heightmap heightmap = chunk.getOrCreateHeightmapUnprimed(Heightmap.Types.OCEAN_FLOOR_WG);
        Heightmap heightmap1 = chunk.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE_WG);

        for (int i = 0; i < 64; i++) {
            BlockState blockstate = customBlock;
            if (blockstate != null) {
                int j = chunk.getMinY() + i;

                for (int k = 0; k < 16; k++) {
                    for (int l = 0; l < 16; l++) {

                        chunk.setBlockState(blockpos$mutableblockpos.set(k, j, l), (k == 0 || l == 0) ? Blocks.BEDROCK.defaultBlockState() :blockstate, false);
                        heightmap.update(k, j, l, blockstate);
                        heightmap1.update(k, j, l, blockstate);
                    }
                }
            }
        }

        return CompletableFuture.completedFuture(chunk);
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
