package com.wanderersoftherift.wotr.world.level.levelgen.processor.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.wanderersoftherift.wotr.mixin.InvokerBlockBehaviour;
import com.wanderersoftherift.wotr.util.RandomSourceFromJavaRandom;
import com.wanderersoftherift.wotr.world.level.FastRiftGenerator;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static net.minecraft.world.level.block.Blocks.JIGSAW;

public class ProcessorUtil {
    public static final String NBT_FINAL_STATE = "final_state";

    private static final LoadingCache<TagKey<Item>, List<Block>> ITEM_TAG_BLOCK_CACHE = CacheBuilder.newBuilder()
            .maximumSize(5)
            .build(new CacheLoader<>() {
                @Override
                public List<Block> load(TagKey<Item> tagKey) {
                    return getBlocksFromItemTag(tagKey);
                }
            });

    private static final LoadingCache<TagKey<Block>, List<Block>> BLOCK_TAG_BLOCK_CACHE = CacheBuilder.newBuilder()
            .maximumSize(5)
            .build(new CacheLoader<>() {
                @Override
                public List<Block> load(TagKey<Block> tagKey) {
                    return getBlocksFromBlockTag(tagKey);
                }
            });

    private static final HashMap<Long, RandomSource> RANDOM_SEED_CACHE = new HashMap<>();

    private static long getRiftSeed(LevelAccessor level) {

        var riftSeed = 0L;
        if (((ServerChunkCache) level.getChunkSource()).getGenerator() instanceof FastRiftGenerator riftGenerator) {
            var cfgSeed = riftGenerator.getRiftConfig().riftGen().seed();
            if (cfgSeed.isPresent()) {
                riftSeed = cfgSeed.get();
            }
        }
        return riftSeed;
    }

    public static PositionalRandomFactory getRiftRandomFactory(LevelAccessor level, long salt) {
        return RandomSourceFromJavaRandom.positional(RandomSourceFromJavaRandom.get(6), getRiftSeed(level) + salt);
    }

    public static RandomSource getRandom(
            StructureRandomType type,
            BlockPos blockPos,
            BlockPos piecePos,
            BlockPos structurePos,
            LevelReader world,
            long processorSeed) {
        RandomSource randomSource = new RandomSourceFromJavaRandom(RandomSourceFromJavaRandom.get(0),
                getRandomSeed(type, blockPos, piecePos, structurePos, world, processorSeed));
        return randomSource;
    }

    public static long getRandomSeed(
            StructureRandomType type,
            BlockPos blockPos,
            BlockPos piecePos,
            BlockPos structurePos,
            LevelReader world,
            long processorSeed) {
        var riftSeed = getRiftSeed((LevelAccessor) world);
        return switch (type) {
            case BLOCK -> getRandomSeed(blockPos, processorSeed) + riftSeed;
            case PIECE -> getRandomSeed(piecePos, processorSeed) + riftSeed;
            case STRUCTURE -> getRandomSeed(structurePos, processorSeed) + riftSeed;
            case WORLD -> ((WorldGenLevel) world).getSeed() + processorSeed + riftSeed;
        };
    }

    public static long getRandomSeed(BlockPos pos, long processorSeed) {
        if (pos == null) {
            return Util.getMillis() + processorSeed;
        } else {
            return Mth.getSeed(pos) + processorSeed;
        }
    }

    public static RandomSource getRandom(
            StructureRandomType type,
            BlockPos blockPos,
            BlockPos piecePos,
            BlockPos structurePos,
            LevelReader world,
            Optional<Long> processorSeed) {
        return switch (type) {
            case BLOCK -> RANDOM_SEED_CACHE.computeIfAbsent(structurePos.asLong(),
                    key -> createRandom(getRandomSeed(blockPos, 0L)));
            case PIECE -> createRandom(getRandomSeed(piecePos, processorSeed.orElse(0L)));
            case STRUCTURE -> createRandom(getRandomSeed(structurePos, processorSeed.orElse(0L)));
            case WORLD -> createRandom(((WorldGenLevel) world).getSeed() + processorSeed.orElse(0L));
        };
    }

    public static RandomSource createRandom(Long processorSeed) {
        RandomSource randomSource = RandomSource.create(processorSeed);
        randomSource.consumeCount(3);
        return randomSource;
    }

    public static Block getRandomBlockFromBlockTag(
            TagKey<Block> tagKey,
            RandomSource random,
            List<Block> exclusionList) {
        List<Block> blockList = BLOCK_TAG_BLOCK_CACHE.getUnchecked(tagKey);
        return getRandomBlockFromBlockList(random, exclusionList, blockList);
    }

    public static Block getRandomBlockFromItemTag(TagKey<Item> tagKey, RandomSource random, List<Block> exclusionList) {
        List<Block> blockList = ITEM_TAG_BLOCK_CACHE.getUnchecked(tagKey);
        return getRandomBlockFromBlockList(random, exclusionList, blockList);
    }

    private static Block getRandomBlockFromBlockList(
            RandomSource random,
            List<Block> exclusionList,
            List<Block> blockList) {
        List<Block> collect = blockList.stream().filter(block -> !exclusionList.contains(block)).toList();
        if (!collect.isEmpty()) {
            return collect.get(random.nextInt(collect.size()));
        }
        return Blocks.AIR;
    }

    public static List<Block> getBlocksFromItemTag(TagKey<Item> tag) {
        return BuiltInRegistries.ITEM.get(tag)
                .map(it -> it.stream()
                        .map(Holder::value)
                        .filter(item -> item instanceof BlockItem)
                        .map(item -> ((BlockItem) item).getBlock())
                        .toList())
                .orElseGet(List::of);
    }

    public static List<Block> getBlocksFromBlockTag(TagKey<Block> tag) {
        return BuiltInRegistries.BLOCK.get(tag).map(it -> it.stream().map(Holder::value).toList()).orElseGet(List::of);
    }

    public static StructureTemplate.StructureBlockInfo getBlock(
            List<StructureTemplate.StructureBlockInfo> pieceBlocks,
            BlockPos pos) {
        return pieceBlocks.stream().filter(blockInfo -> blockInfo.pos().equals(pos)).findFirst().orElse(null);
    }

    public static boolean isSolid(StructureTemplate.StructureBlockInfo blockinfo) {
        if (blockinfo != null && blockinfo.state().is(JIGSAW)) {
            Block block = BuiltInRegistries.BLOCK
                    .getValue(ResourceLocation.parse(blockinfo.nbt().getString(NBT_FINAL_STATE)));
            return block != null && !block.defaultBlockState().isAir() && !(block instanceof LiquidBlock);
        } else {
            return blockinfo != null && !blockinfo.state().isAir()
                    && !(blockinfo.state().getBlock() instanceof LiquidBlock);
        }
    }

    public static boolean isFaceFull(StructureTemplate.StructureBlockInfo blockinfo, Direction direction) {
        if (blockinfo == null) {
            return false;
        }
        if (blockinfo.state().is(JIGSAW)) {
            Block block = BuiltInRegistries.BLOCK
                    .getValue(ResourceLocation.parse(blockinfo.nbt().getString(NBT_FINAL_STATE)));
            return isFaceFullFast(block.defaultBlockState(), blockinfo.pos(), direction);
        } else {
            return isFaceFullFast(blockinfo.state(), blockinfo.pos(), direction);
        }
    }

    public static boolean isFaceFullFast(BlockState state, BlockPos pos, Direction direction) {
        if (state.isAir() || state.getBlock() instanceof LiquidBlock) {
            return false;
        }
        VoxelShape overallShape = ((InvokerBlockBehaviour) state.getBlock()).invokeGetShape(state, null, pos,
                CollisionContext.empty());
        if (overallShape == Shapes.block()) {
            return true;
        }
        if (overallShape == Shapes.empty()) {
            return false;
        }
        return Block.isFaceFull(overallShape, direction);
    }

    public static VoxelShape shapeForFaceFullCheck(BlockState state, BlockPos pos) {
        if (state.isAir() || state.getBlock() instanceof LiquidBlock) {
            return null;
        }
        return ((InvokerBlockBehaviour) state.getBlock()).invokeGetShape(state, null, pos, CollisionContext.empty());
    }

    public static boolean isFaceFullFast(VoxelShape overallShape, Direction direction) {
        if (overallShape == null) {
            return false;
        }
        if (overallShape == Shapes.block()) {
            return true;
        }
        if (overallShape == Shapes.empty()) {
            return false;
        }
        return Block.isFaceFull(overallShape, direction);
    }

    public static StructureTemplate.Palette getCurrentPalette(List<StructureTemplate.Palette> palettes) {
        int i = palettes.size();
        if (i == 0) {
            throw new IllegalStateException("No palettes");
        } else {
            return palettes.get(i - 1);
        }
    }

    public static StructureTemplate.StructureBlockInfo getBlockInfo(
            List<StructureTemplate.StructureBlockInfo> mapByPos,
            BlockPos pos) {
        int index = getBlockIndex(mapByPos, pos);
        if (index < 0) {
            return null;
        }
        return mapByPos.get(index);
    }

    public static int getBlockIndex(List<StructureTemplate.StructureBlockInfo> mapByPos, BlockPos pos) {
        BlockPos firstPos = mapByPos.getFirst().pos();
        BlockPos lastPos = mapByPos.getLast().pos();
        if (!isPosBetween(pos, firstPos, lastPos)) {
            return -1;
        }
        int width = lastPos.getX() + 1 - firstPos.getX();
        int depth = lastPos.getZ() + 1 - firstPos.getZ();
        int index = (pos.getX() - firstPos.getX()) + (pos.getZ() - firstPos.getZ()) * width
                + (pos.getY() - firstPos.getY()) * width * depth;
        if (index < 0 || index >= mapByPos.size()) {
            return -1;
        }
        return index;
    }

    private static boolean isPosBetween(BlockPos pos, BlockPos firstPos, BlockPos lastPos) {
        return pos.getX() >= firstPos.getX() && pos.getX() <= lastPos.getX() && pos.getY() >= firstPos.getY()
                && pos.getY() <= lastPos.getY() && pos.getZ() >= firstPos.getZ() && pos.getZ() < lastPos.getZ();
    }

    public static BlockState copyState(BlockState fromState, BlockState toState) {
        var values = fromState.getBlock().defaultBlockState().getValues();
        if (values.isEmpty()) {
            return toState;
        }
        for (var property : values.keySet()) {
            toState = updateProperty(fromState, toState, property);
        }
        return toState;
    }

    private static <T extends Comparable<T>> BlockState updateProperty(
            BlockState state,
            BlockState newState,
            Property<T> property) {
        if (newState.hasProperty(property)) {
            return newState.setValue(property, state.getValue(property));
        }
        return newState;
    }
}