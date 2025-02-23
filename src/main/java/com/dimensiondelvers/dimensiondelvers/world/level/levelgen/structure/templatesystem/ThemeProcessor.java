package com.dimensiondelvers.dimensiondelvers.world.level.levelgen.structure.templatesystem;

import com.dimensiondelvers.dimensiondelvers.DimensionDelvers;
import com.dimensiondelvers.dimensiondelvers.world.level.levelgen.structure.templatesystem.util.ProcessorUtil;
import com.dimensiondelvers.dimensiondelvers.world.level.levelgen.structure.templatesystem.util.StructureRandomType;
import com.dimensiondelvers.dimensiondelvers.world.level.levelgen.theme.LevelRiftThemeData;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.*;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static com.dimensiondelvers.dimensiondelvers.init.ModProcessors.RIFT_THEME;

public class ThemeProcessor extends StructureProcessor {
    public static final MapCodec<ThemeProcessor> CODEC = MapCodec.unit(ThemeProcessor::new);

    private static final long SEED = 268431L;

    public ThemeProcessor() {
    }

    @Override
    public StructureTemplate.StructureBlockInfo process(LevelReader world, BlockPos piecePos, BlockPos structurePos, StructureTemplate.StructureBlockInfo rawBlockInfo, StructureTemplate.StructureBlockInfo blockInfo, StructurePlaceSettings settings, @Nullable StructureTemplate template) {
        List<StructureProcessor> processors = getThemeProcessors(world, piecePos, structurePos);
        Iterator<StructureProcessor> iterator = processors.iterator();

        while (blockInfo != null && iterator.hasNext()) {
            blockInfo = iterator.next()
                    .process(world, piecePos, structurePos, rawBlockInfo, blockInfo, settings, template);
        }
        return blockInfo;
    }

    @Override
    public List<StructureTemplate.StructureBlockInfo> finalizeProcessing(ServerLevelAccessor serverLevel, BlockPos piecePos, BlockPos structurePos, List<StructureTemplate.StructureBlockInfo> originalBlockInfos, List<StructureTemplate.StructureBlockInfo> processedBlockInfos, StructurePlaceSettings settings) {
        List<StructureProcessor> processors = getThemeProcessors(serverLevel, piecePos, structurePos);
        Iterator<StructureProcessor> iterator = processors.iterator();

        List<StructureTemplate.StructureBlockInfo> list1 = processedBlockInfos;

        for (StructureProcessor structureprocessor : getThemeProcessors(serverLevel, piecePos, structurePos)) {
            list1 = structureprocessor.finalizeProcessing(serverLevel, piecePos, structurePos, originalBlockInfos, list1, settings);
        }

        return list1;
    }

    private List<StructureProcessor> getThemeProcessors(LevelReader world, BlockPos piecePos, BlockPos structurePos) {
        if(world instanceof ServerLevel serverLevel) {
            LevelRiftThemeData riftThemeData = LevelRiftThemeData.getFromLevel(serverLevel);
            if(riftThemeData.getTheme() != null) {
                return riftThemeData.getTheme().processors().value().list();
            }
            return randomThemeProcessors(serverLevel, piecePos, structurePos);
        }
        return new ArrayList<>();
    }

    private List<StructureProcessor> randomThemeProcessors(ServerLevel world, BlockPos piecePos, BlockPos structurePos) {
        Optional<Registry<StructureProcessorList>> registryReference = world.registryAccess().lookup(Registries.PROCESSOR_LIST);
        if (registryReference.isPresent()) {
            RandomSource random = ProcessorUtil.getRandom(StructureRandomType.STRUCTURE, structurePos, piecePos, structurePos, world, SEED);
            List<StructureProcessorList> processorLists = new ArrayList<>();
            //processorLists.add(registryReference.get().get(ResourceLocation.fromNamespaceAndPath(DimensionDelvers.MODID, "forest")).get().value());
            processorLists.add(registryReference.get().get(ResourceLocation.fromNamespaceAndPath(DimensionDelvers.MODID, "cave")).get().value());
            return processorLists.get(random.nextInt(processorLists.size())).list();
        }
        return new ArrayList<>();
    }

    protected StructureProcessorType<?> getType() {
        return RIFT_THEME.get();
    }

}