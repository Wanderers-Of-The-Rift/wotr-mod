package com.wanderersoftherift.wotr.world.level.levelgen.processor.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.wanderersoftherift.wotr.mixin.InvokerBlockBehaviour;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static net.minecraft.world.level.block.Blocks.JIGSAW;

public class ProcessorUtil {
    public static final String NBT_FINAL_STATE = "final_state";

    public static RandomSource getRandom(StructureRandomType type, BlockPos blockPos, BlockPos piecePos,
            BlockPos structurePos, LevelReader world, long processorSeed) {
        RandomSource randomSource = RandomSource
                .create(getRandomSeed(type, blockPos, piecePos, structurePos, world, processorSeed));
        randomSource.consumeCount(3);
        return randomSource;
    }

    public static long getRandomSeed(StructureRandomType type, BlockPos blockPos, BlockPos piecePos,
            BlockPos structurePos, LevelReader world, long processorSeed) {
        return switch (type) {
            case BLOCK -> getRandomSeed(blockPos, processorSeed);
            case PIECE -> getRandomSeed(piecePos, processorSeed);
            case STRUCTURE -> getRandomSeed(structurePos, processorSeed);
            case WORLD -> ((WorldGenLevel) world).getSeed() + processorSeed;
        };
    }

    public static long getRandomSeed(BlockPos pos, long processorSeed) {
        if (pos == null) {
            return Util.getMillis() + processorSeed;
        } else {
            return Mth.getSeed(pos) + processorSeed;
        }
    }

    private static final HashMap<Long, RandomSource> RANDOM_SEED_CACHE = new HashMap<>();

    public static RandomSource getRandom(StructureRandomType type, BlockPos blockPos, BlockPos piecePos,
            BlockPos structurePos, LevelReader world, Optional<Long> processorSeed) {
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

    public static Block getRandomBlockFromBlockTag(TagKey<Block> tagKey, RandomSource random,
            List<Block> exclusionList) {
        List<Block> blockList = BLOCK_TAG_BLOCK_CACHE.getUnchecked(tagKey);
        return getRandomBlockFromBlockList(random, exclusionList, blockList);
    }

    public static Block getRandomBlockFromItemTag(TagKey<Item> tagKey, RandomSource random, List<Block> exclusionList) {
        List<Block> blockList = ITEM_TAG_BLOCK_CACHE.getUnchecked(tagKey);
        return getRandomBlockFromBlockList(random, exclusionList, blockList);
    }

    private static Block getRandomBlockFromBlockList(RandomSource random, List<Block> exclusionList,
            List<Block> blockList) {
        List<Block> collect = blockList.stream().filter(block -> !exclusionList.contains(block)).toList();
        if (!collect.isEmpty()) {
            return collect.get(random.nextInt(collect.size()));
        }
        return Blocks.AIR;
    }

    private static final LoadingCache<TagKey<Item>, List<Block>> ITEM_TAG_BLOCK_CACHE = CacheBuilder.newBuilder()
            .maximumSize(5)
            .build(new CacheLoader<>() {
                @Override
                public List<Block> load(TagKey<Item> tagKey) {
                    return getBlocksFromItemTag(tagKey);
                }
            });

    public static List<Block> getBlocksFromItemTag(TagKey<Item> tag) {
        return BuiltInRegistries.ITEM.get(tag)
                .map(it -> it.stream()
                        .map(Holder::value)
                        .filter(item -> item instanceof BlockItem)
                        .map(item -> ((BlockItem) item).getBlock())
                        .toList())
                .orElseGet(List::of);
    }

    private static final LoadingCache<TagKey<Block>, List<Block>> BLOCK_TAG_BLOCK_CACHE = CacheBuilder.newBuilder()
            .maximumSize(5)
            .build(new CacheLoader<>() {
                @Override
                public List<Block> load(TagKey<Block> tagKey) {
                    return getBlocksFromBlockTag(tagKey);
                }
            });

    public static List<Block> getBlocksFromBlockTag(TagKey<Block> tag) {
        return BuiltInRegistries.BLOCK.get(tag).map(it -> it.stream().map(Holder::value).toList()).orElseGet(List::of);
    }

    /*
     * public static Item getRandomItemFromTag(TagKey<Item> tag, RandomSource random, List<ResourceLocation>
     * exclusionList){ List<Item> resultList = ITEMS.tags().getTag(tag).stream().filter(item ->
     * !exclusionList.contains(ITEMS.getKey(item))).collect(Collectors.toList()); return
     * resultList.get(random.nextInt(resultList.size())); }
     */

    public static @NotNull BlockState copyStairsState(BlockState blockState, BlockState newBlockState) {
        newBlockState = updateProperty(blockState, newBlockState, StairBlock.FACING);
        newBlockState = updateProperty(blockState, newBlockState, StairBlock.SHAPE);
        newBlockState = updateProperty(blockState, newBlockState, StairBlock.HALF);
        newBlockState = updateProperty(blockState, newBlockState, StairBlock.WATERLOGGED);
        return newBlockState;
    }

    public static @NotNull BlockState copySlabState(BlockState blockState, BlockState newBlockState) {
        newBlockState = updateProperty(blockState, newBlockState, SlabBlock.TYPE);
        newBlockState = updateProperty(blockState, newBlockState, SlabBlock.WATERLOGGED);
        return newBlockState;
    }

    public static @NotNull BlockState copyWallState(BlockState blockState, BlockState newBlockState) {
        newBlockState = updateProperty(blockState, newBlockState, WallBlock.UP);
        newBlockState = updateProperty(blockState, newBlockState, WallBlock.EAST_WALL);
        newBlockState = updateProperty(blockState, newBlockState, WallBlock.NORTH_WALL);
        newBlockState = updateProperty(blockState, newBlockState, WallBlock.SOUTH_WALL);
        newBlockState = updateProperty(blockState, newBlockState, WallBlock.WEST_WALL);
        newBlockState = updateProperty(blockState, newBlockState, WallBlock.WATERLOGGED);
        return newBlockState;
    }

    private static BlockState updateProperty(BlockState state, BlockState newState, Property property) {
        if (newState.hasProperty(property)) {
            return newState.setValue(property, state.getValue(property));
        }
        return newState;
    }

    public static Map<BlockPos, StructureTemplate.StructureBlockInfo> mapByPos(
            List<StructureTemplate.StructureBlockInfo> pieceBlocks) {
        return pieceBlocks.stream()
                .collect(Collectors.toMap(StructureTemplate.StructureBlockInfo::pos, blockInfo -> blockInfo));
    }

    public static StructureTemplate.StructureBlockInfo getBlock(List<StructureTemplate.StructureBlockInfo> pieceBlocks,
            BlockPos pos) {
        return pieceBlocks.stream().filter(blockInfo -> blockInfo.pos().equals(pos)).findFirst().orElse(null);
    }

    /*
     * public static boolean isAir(StructureTemplate.StructureBlockInfo blockinfo){ if(blockinfo != null &&
     * blockinfo.state().is(JIGSAW)){ Block block = BuiltInRegistries.BLOCKS.getValue(new
     * ResourceLocation(blockinfo.nbt.getString(NBT_FINAL_STATE))); return block == null ||
     * block.defaultBlockState().isAir() ; }else { return blockinfo == null || blockinfo.state.is(AIR) ||
     * blockinfo.state.is(CAVE_AIR); } }
     */

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
        if (blockinfo == null) return false;
        if (blockinfo.state().is(JIGSAW)) {
            Block block = BuiltInRegistries.BLOCK
                    .getValue(ResourceLocation.parse(blockinfo.nbt().getString(NBT_FINAL_STATE)));
            return isFaceFullFast(block.defaultBlockState(), blockinfo.pos(), direction);
        } else {
            return isFaceFullFast(blockinfo.state(), blockinfo.pos(), direction);
        }
    }

    private static boolean isFaceFullFast(BlockState state, BlockPos pos, Direction direction) {
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

    public static StructureTemplate.Palette getCurrentPalette(List<StructureTemplate.Palette> palettes) {
        int i = palettes.size();
        if (i == 0) {
            throw new IllegalStateException("No palettes");
        } else {
            return palettes.get(i - 1);
        }
    }

    public static StructureTemplate.StructureBlockInfo getBlockInfo(List<StructureTemplate.StructureBlockInfo> mapByPos,
            BlockPos pos) {
        BlockPos firstPos = mapByPos.getFirst().pos();
        BlockPos lastPos = mapByPos.getLast().pos();
        if (!isPosBetween(pos, firstPos, lastPos)) {
            return null;
        }
        int width = lastPos.getX() + 1 - firstPos.getX();
        int height = lastPos.getY() + 1 - firstPos.getY();
        int index = (pos.getX() - firstPos.getX()) + (pos.getY() - firstPos.getY()) * width
                + (pos.getZ() - firstPos.getZ()) * width * height;
        if (index < 0 || index >= mapByPos.size()) {
            return null;
        }
        return mapByPos.get(index);
    }

    private static boolean isPosBetween(BlockPos pos, BlockPos firstPos, BlockPos lastPos) {
        return pos.getX() > firstPos.getX() && pos.getX() < lastPos.getX() && pos.getY() > firstPos.getY()
                && pos.getY() < lastPos.getY() && pos.getZ() > firstPos.getZ() && pos.getZ() < lastPos.getZ();
    }

    public static BlockState copyState(BlockState fromState, BlockState toState) {
        if (fromState.is(BlockTags.STAIRS)) {
            return copyStairsState(fromState, toState);
        } else if (fromState.is(BlockTags.SLABS)) {
            return copySlabState(fromState, toState);
        } else if (fromState.is(BlockTags.WALLS)) {
            return copyWallState(fromState, toState);
        } else {
            return toState;
        }
    }
}