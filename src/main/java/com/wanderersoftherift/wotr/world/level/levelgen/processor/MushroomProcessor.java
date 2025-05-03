package com.wanderersoftherift.wotr.world.level.levelgen.processor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.init.ModProcessors;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.util.ProcessorUtil;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.util.StructureRandomType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.wanderersoftherift.wotr.world.level.levelgen.processor.util.ProcessorUtil.getBlockInfo;
import static com.wanderersoftherift.wotr.world.level.levelgen.processor.util.ProcessorUtil.getRandomBlockFromItemTag;
import static com.wanderersoftherift.wotr.world.level.levelgen.processor.util.ProcessorUtil.isFaceFull;
import static com.wanderersoftherift.wotr.world.level.levelgen.processor.util.StructureRandomType.RANDOM_TYPE_CODEC;
import static net.minecraft.core.Direction.DOWN;
import static net.minecraft.core.Direction.UP;
import static net.neoforged.neoforge.common.Tags.Items.MUSHROOMS;

public class MushroomProcessor extends StructureProcessor {
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

    public MushroomProcessor(List<Block> exclusionList, float rarity, StructureRandomType structureRandomType,
            StructureRandomType tagStructureRandomType) {
        this.exclusionList = exclusionList;
        this.rarity = rarity;
        this.structureRandomType = structureRandomType;
        this.tagStructureRandomType = tagStructureRandomType;
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
        return ModProcessors.MUSHROOMS.get();
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
}
