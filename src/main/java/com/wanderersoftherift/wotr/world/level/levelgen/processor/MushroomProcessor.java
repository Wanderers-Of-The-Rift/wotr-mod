package com.wanderersoftherift.wotr.world.level.levelgen.processor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.init.worldgen.WotrProcessors;
import com.wanderersoftherift.wotr.util.FastRandomSource;
import com.wanderersoftherift.wotr.world.level.levelgen.RiftProcessedRoom;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.util.ProcessorUtil;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.util.StructureRandomType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.wanderersoftherift.wotr.world.level.levelgen.processor.util.ProcessorUtil.createRandom;
import static com.wanderersoftherift.wotr.world.level.levelgen.processor.util.ProcessorUtil.getBlockInfo;
import static com.wanderersoftherift.wotr.world.level.levelgen.processor.util.ProcessorUtil.getRandomBlockFromItemTag;
import static com.wanderersoftherift.wotr.world.level.levelgen.processor.util.ProcessorUtil.getRandomSeed;
import static com.wanderersoftherift.wotr.world.level.levelgen.processor.util.ProcessorUtil.isFaceFull;
import static com.wanderersoftherift.wotr.world.level.levelgen.processor.util.ProcessorUtil.isFaceFullFast;
import static com.wanderersoftherift.wotr.world.level.levelgen.processor.util.StructureRandomType.BLOCK;
import static com.wanderersoftherift.wotr.world.level.levelgen.processor.util.StructureRandomType.RANDOM_TYPE_CODEC;
import static net.minecraft.core.Direction.DOWN;
import static net.minecraft.core.Direction.UP;
import static net.neoforged.neoforge.common.Tags.Items.MUSHROOMS;

public class MushroomProcessor extends StructureProcessor
        implements RiftAdjacencyProcessor<MushroomProcessor.ReplacementData> {
    public static final MapCodec<MushroomProcessor> CODEC = RecordCodecBuilder
            .mapCodec(builder -> builder
                    .group(BuiltInRegistries.BLOCK.byNameCodec()
                            .listOf()
                            .optionalFieldOf("exclusion_list", Collections.emptyList())
                            .forGetter(MushroomProcessor::getExclusionList),
                            Codec.FLOAT.fieldOf("rarity").forGetter(MushroomProcessor::getRarity),
                            RANDOM_TYPE_CODEC.optionalFieldOf("random_type", StructureRandomType.BLOCK)
                                    .forGetter(MushroomProcessor::getStructureRandomType),
                            RANDOM_TYPE_CODEC.optionalFieldOf("tag_random_type", StructureRandomType.PIECE)
                                    .forGetter(MushroomProcessor::getTagStructureRandomType))
                    .apply(builder, MushroomProcessor::new));
    private static final Optional<Long> SEED = Optional.empty();

    private final TagKey<Item> itemTag = MUSHROOMS;
    private final List<Block> exclusionList;
    private final float rarity;
    private final StructureRandomType structureRandomType;
    private final StructureRandomType tagStructureRandomType;
    private final PositionalRandomFactory rngFactory;

    public MushroomProcessor(List<Block> exclusionList, float rarity, StructureRandomType structureRandomType,
            StructureRandomType tagStructureRandomType) {
        this.exclusionList = exclusionList;
        this.rarity = rarity;
        this.structureRandomType = structureRandomType;
        this.tagStructureRandomType = tagStructureRandomType;
        rngFactory = FastRandomSource.positional(SEED.orElse(33125144131546418L));
    }

    @Override
    public List<StructureTemplate.StructureBlockInfo> finalizeProcessing(
            ServerLevelAccessor serverLevel,
            BlockPos offset,
            BlockPos pos,
            List<StructureTemplate.StructureBlockInfo> originalBlockInfos,
            List<StructureTemplate.StructureBlockInfo> processedBlockInfos,
            StructurePlaceSettings settings) {
        List<StructureTemplate.StructureBlockInfo> newBlockInfos = new ArrayList<>(processedBlockInfos.size());
        for (StructureTemplate.StructureBlockInfo blockInfo : processedBlockInfos) {
            newBlockInfos.add(processFinal(serverLevel, offset, pos, blockInfo, settings, processedBlockInfos));
        }
        return newBlockInfos;
    }

    public StructureTemplate.StructureBlockInfo processFinal(
            LevelReader world,
            BlockPos piecePos,
            BlockPos structurePos,
            StructureTemplate.StructureBlockInfo blockInfo,
            StructurePlaceSettings settings,
            List<StructureTemplate.StructureBlockInfo> mapByPos) {
        RandomSource random = ProcessorUtil.getRandom(structureRandomType, blockInfo.pos(), piecePos, structurePos,
                world, SEED);
        BlockState blockstate = blockInfo.state();
        BlockPos blockpos = blockInfo.pos();
        if (blockstate.isAir() && random.nextFloat() <= rarity) {
            if (isFaceFull(getBlockInfo(mapByPos, blockInfo.pos().relative(DOWN)), UP)) {
                RandomSource tagRandom = ProcessorUtil.getRandom(tagStructureRandomType, blockInfo.pos(), piecePos,
                        structurePos, world, SEED);
                Block block = getRandomBlockFromItemTag(itemTag, tagRandom, exclusionList);
                return new StructureTemplate.StructureBlockInfo(blockpos, block.defaultBlockState(), blockInfo.nbt());
            }
        }
        return blockInfo;
    }

    protected StructureProcessorType<?> getType() {
        return WotrProcessors.MUSHROOMS.get();
    }

    public List<Block> getExclusionList() {
        return exclusionList;
    }

    public float getRarity() {
        return rarity;
    }

    public StructureRandomType getStructureRandomType() {
        return structureRandomType;
    }

    public StructureRandomType getTagStructureRandomType() {
        return tagStructureRandomType;
    }

    // @Override
    public void finalizeRoomProcessing(
            RiftProcessedRoom room,
            ServerLevelAccessor world,
            BlockPos structurePos,
            Vec3i pieceSize) {

        var blockRandomFlag = structureRandomType == BLOCK;
        RandomSource random = createRandom(getRandomSeed(structurePos, SEED.orElse(0L)));
        // ProcessorUtil.getRandom(blockRandomFlag ? PIECE : structureRandomType, null, structurePos, new
        // BlockPos(0,0,0), world, SEED);
        var blockRandomFlag2 = tagStructureRandomType == BLOCK;
        RandomSource random2 = createRandom(getRandomSeed(structurePos, SEED.orElse(0L)));
        // ProcessorUtil.getRandom(blockRandomFlag ? PIECE : structureRandomType, null, structurePos, new
        // BlockPos(0,0,0), world, SEED);
        var roll = random.nextFloat();
        var roll2 = getRandomBlockFromItemTag(itemTag, random2, exclusionList);
        var bp = new BlockPos.MutableBlockPos();
        for (int x = 0; x < pieceSize.getX(); x++) {
            for (int z = 0; z < pieceSize.getZ(); z++) {
                for (int y = 0; y < pieceSize.getY(); y++) {
                    var x2 = x + structurePos.getX();
                    var y2 = y + structurePos.getY();
                    var z2 = z + structurePos.getZ();
                    var currentState = room.getBlock(x2, y2, z2);
                    if (currentState != null && currentState.isAir()) {
                        if (blockRandomFlag) {
                            roll = random.nextFloat();
                        }
                        if (roll <= rarity) {
                            if (blockRandomFlag2) {
                                roll2 = getRandomBlockFromItemTag(itemTag, random2, exclusionList);
                            }
                            var down = room.getBlock(x2, y2 - 1, z2);
                            bp.set(x2, y2 - 1, z2);
                            boolean validDown = down == null || isFaceFullFast(down, bp, Direction.UP);
                            if (!validDown) {
                                continue;
                            }
                            room.setBlock(x2, y2, z2, roll2.defaultBlockState());

                        }
                    }
                }
            }
        }
    }

    @Override
    public int processAdjacency(ReplacementData data, BlockState[] directions, boolean isHidden) {
        var old = directions[6];
        if (!isHidden && !old.isAir()) {
            var up = directions[1];
            boolean validUp = (up == null || up.isAir()) && data.recalculateChance() <= rarity
                    && isFaceFullFast(old, BlockPos.ZERO, Direction.UP);
            if (validUp) {
                directions[1] = data.recalculateBlock().defaultBlockState();
                return 2;
            }
        }
        return 0;
    }

    @Override
    public ReplacementData createData(BlockPos structurePos, Vec3i pieceSize, ServerLevelAccessor world) {
        return new ReplacementData(
                itemTag, exclusionList, rngFactory.at(structurePos.getX(), structurePos.getY(), structurePos.getZ()),
                rngFactory.at(structurePos.getX(), structurePos.getY(), structurePos.getZ()),
                structureRandomType == BLOCK, tagStructureRandomType == BLOCK
        );
    }

    public static class ReplacementData {
        private final TagKey<Item> tagKey;
        private final List<Block> exclusionList;
        private final RandomSource rng1;
        private final RandomSource rng2;
        private final boolean isRng1PerBlock;
        private final boolean isRng2PerBlock;
        private float roll1;
        private Block roll2;

        public ReplacementData(TagKey<Item> tagKey, List<Block> exclusionList, RandomSource rng1, RandomSource rng2,
                boolean isRng1PerBlock, boolean isRng2PerBlock) {
            this.tagKey = tagKey;
            this.exclusionList = exclusionList;
            this.rng1 = rng1;
            this.rng2 = rng2;
            this.isRng1PerBlock = isRng1PerBlock;
            this.isRng2PerBlock = isRng2PerBlock;
            roll1 = rng1.nextFloat();
            roll2 = getRandomBlockFromItemTag(tagKey, rng2, exclusionList);
        }

        public float recalculateChance() {
            if (isRng1PerBlock) {
                roll1 = rng1.nextFloat();
            }
            return roll1;
        }

        public Block recalculateBlock() {
            if (isRng2PerBlock) {
                roll2 = getRandomBlockFromItemTag(tagKey, rng2, exclusionList);
            }
            return roll2;
        }
    }
}
