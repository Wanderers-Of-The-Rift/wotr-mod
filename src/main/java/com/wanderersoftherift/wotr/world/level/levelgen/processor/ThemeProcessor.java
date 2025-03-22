package com.wanderersoftherift.wotr.world.level.levelgen.processor;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.ModRiftThemes;
import com.wanderersoftherift.wotr.world.level.levelgen.theme.LevelRiftThemeData;
import com.wanderersoftherift.wotr.world.level.levelgen.theme.RiftTheme;
import com.wanderersoftherift.wotr.world.level.levelgen.theme.ThemePieceType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static com.wanderersoftherift.wotr.init.ModProcessors.RIFT_THEME;

public class ThemeProcessor extends StructureProcessor {
    public static final MapCodec<ThemeProcessor> CODEC = RecordCodecBuilder.mapCodec(builder ->
            builder.group(
                    ThemePieceType.CODEC.fieldOf("piece_type").forGetter(ThemeProcessor::getThemePieceType)
            ).apply(builder, ThemeProcessor::new));

    private ThemePieceType themePieceType;

    public ThemeProcessor(ThemePieceType themePieceType) {
        this.themePieceType = themePieceType;
    }

    public ThemePieceType getThemePieceType() {
        return themePieceType;
    }

    @Override
    public StructureTemplate.StructureBlockInfo process(LevelReader world, BlockPos piecePos, BlockPos structurePos, StructureTemplate.StructureBlockInfo rawBlockInfo, StructureTemplate.StructureBlockInfo blockInfo, StructurePlaceSettings settings, @Nullable StructureTemplate template) {
        List<StructureProcessor> processors = getThemeProcessors(world, structurePos);
        Iterator<StructureProcessor> iterator = processors.iterator();

        while (blockInfo != null && iterator.hasNext()) {
            blockInfo = iterator.next()
                    .process(world, piecePos, structurePos, rawBlockInfo, blockInfo, settings, template);
        }
        return blockInfo;
    }

    @Override
    public List<StructureTemplate.StructureBlockInfo> finalizeProcessing(ServerLevelAccessor serverLevel, BlockPos piecePos, BlockPos structurePos, List<StructureTemplate.StructureBlockInfo> originalBlockInfos, List<StructureTemplate.StructureBlockInfo> processedBlockInfos, StructurePlaceSettings settings) {
        List<StructureTemplate.StructureBlockInfo> result = processedBlockInfos;

        for (StructureProcessor structureprocessor : getThemeProcessors(serverLevel, structurePos)) {
            result = structureprocessor.finalizeProcessing(serverLevel, piecePos, structurePos, originalBlockInfos, result, settings);
        }

        return result;
    }

    private List<StructureProcessor> getThemeProcessors(LevelReader world, BlockPos structurePos) {
        if(world instanceof ServerLevel serverLevel) {
            LevelRiftThemeData riftThemeData = LevelRiftThemeData.getFromLevel(serverLevel);
            if(riftThemeData.getTheme() != null) {
                return riftThemeData.getTheme().value().getProcessors(themePieceType);
            }
            return defaultThemeProcessors(serverLevel, structurePos);
        }
        return new ArrayList<>();
    }

    private List<StructureProcessor> defaultThemeProcessors(ServerLevel world, BlockPos structurePos) {
        Optional<Registry<RiftTheme>> registryReference = world.registryAccess().lookup(ModRiftThemes.RIFT_THEME_KEY);
        return registryReference.get().get(ResourceLocation.fromNamespaceAndPath(WanderersOfTheRift.MODID, "cave")).get().value().getProcessors(themePieceType);
    }

    protected StructureProcessorType<?> getType() {
        return RIFT_THEME.get();
    }

}