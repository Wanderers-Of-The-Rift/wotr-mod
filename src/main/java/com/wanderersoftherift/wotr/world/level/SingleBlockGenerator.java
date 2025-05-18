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
public class SingleBlockGenerator extends ChunkGenerator {

    public static final MapCodec<SingleBlockGenerator> CODEC = RecordCodecBuilder
            .mapCodec(instance -> instance
                    .group(BiomeSource.CODEC.fieldOf("biome_source").forGetter(SingleBlockGenerator::getBiomeSource),
                            ResourceLocation.CODEC.fieldOf("custom_block")
                                    .forGetter(SingleBlockGenerator::getCustomBlockID))
                    .apply(instance, SingleBlockGenerator::new));

    private final ResourceLocation customBlockID;
    private final BlockState customBlock;

    public SingleBlockGenerator(BiomeSource biomeSource, ResourceLocation defaultBlock) {
        super(biomeSource);
        this.customBlock = BuiltInRegistries.BLOCK.get(defaultBlock)
                .map(Holder.Reference::value)
                .map(Block::defaultBlockState)
                .orElse(AIR.defaultBlockState());
        this.customBlockID = defaultBlock;
    }

    @Override
    protected MapCodec<? extends ChunkGenerator> codec() {
        return CODEC;
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
        return 192;
    }

    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(
            Blender blender,
            RandomState randomState,
            StructureManager structureManager,
            ChunkAccess chunk) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = getMinY(); y < getMinY() + getGenDepth(); y++) {
                    chunk.setBlockState(new BlockPos(x, y, z), customBlock, false);
                }
            }
        }
        return CompletableFuture.completedFuture(chunk);
    }

    @Override
    public int getSeaLevel() {
        return -64;
    }

    @Override
    public int getMinY() {
        return -64;
    }

    @Override
    public int getBaseHeight(int x, int z, Heightmap.Types type, LevelHeightAccessor level, RandomState random) {
        return 192;
    }

    @Override
    public @NotNull NoiseColumn getBaseColumn(int x, int z, LevelHeightAccessor height, RandomState random) {
        return new NoiseColumn(0, new BlockState[] { AIR.defaultBlockState() });
    }

    @Override
    public void addDebugScreenInfo(List<String> info, RandomState random, BlockPos pos) {
        info.add(customBlockID + " generator");
    }

    public ResourceLocation getCustomBlockID() {
        return customBlockID;
    }
}
