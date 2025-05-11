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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import oshi.util.tuples.Pair;

import javax.annotation.Nullable;
import java.lang.ref.PhantomReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.wanderersoftherift.wotr.init.ModProcessors.RIFT_THEME;

public class ThemeProcessor extends StructureProcessor implements RiftTemplateProcessor {
    public static final MapCodec<ThemeProcessor> CODEC = RecordCodecBuilder.mapCodec(builder -> builder
            .group(ThemePieceType.CODEC.fieldOf("piece_type").forGetter(ThemeProcessor::getThemePieceType)
            ).apply(builder, ThemeProcessor::new));

    private ThemePieceType themePieceType;
    private Pair<PhantomReference<LevelReader>,List<RiftTemplateProcessor>> lastThemeTemplateProcessorCache = null;
    private Pair<PhantomReference<LevelReader>,List<Void>> lastThemeRoomProcessorCache = null;

    public ThemeProcessor(ThemePieceType themePieceType) {
        this.themePieceType = themePieceType;
    }

    public ThemePieceType getThemePieceType() {
        return themePieceType;
    }

    @Override
    public StructureTemplate.StructureBlockInfo process(
            LevelReader world,
            BlockPos piecePos,
            BlockPos structurePos,
            StructureTemplate.StructureBlockInfo rawBlockInfo,
            StructureTemplate.StructureBlockInfo blockInfo,
            StructurePlaceSettings settings,
            @Nullable StructureTemplate template) {
        List<StructureProcessor> processors = getThemeProcessors(world, structurePos);

        for (int i = 0; i < processors.size() && blockInfo!=null; i++) {
            blockInfo = processors.get(i).process(world, piecePos, structurePos, rawBlockInfo, blockInfo, settings, template);
        }
        return blockInfo;
    }

    @Override
    public List<StructureTemplate.StructureBlockInfo> finalizeProcessing(
            ServerLevelAccessor serverLevel,
            BlockPos piecePos,
            BlockPos structurePos,
            List<StructureTemplate.StructureBlockInfo> originalBlockInfos,
            List<StructureTemplate.StructureBlockInfo> processedBlockInfos,
            StructurePlaceSettings settings) {
        List<StructureTemplate.StructureBlockInfo> result = processedBlockInfos;

        for (StructureProcessor structureprocessor : getThemeProcessors(serverLevel.getLevel(), structurePos)) {
            result = structureprocessor.finalizeProcessing(serverLevel, piecePos, structurePos, originalBlockInfos,
                    result, settings);
        }

        return result;
    }

    private List<StructureProcessor> getThemeProcessors(LevelReader world, BlockPos structurePos) {
        if(world instanceof ServerLevel serverLevel) {
            LevelRiftThemeData riftThemeData = LevelRiftThemeData.getFromLevel(serverLevel);
            var result = (riftThemeData.getTheme() != null)?
                    riftThemeData.getTheme().value().getProcessors(themePieceType)
                    : defaultThemeProcessors(serverLevel, structurePos);
            return result;
        }
        return new ArrayList<>();
    }


    private List<RiftTemplateProcessor> getThemeTemplateProcessors(LevelReader world, BlockPos structurePos) {
        var currentCache = lastThemeTemplateProcessorCache;
        if(world!=null && currentCache!=null && currentCache.getA().refersTo(world)) return currentCache.getB();
        if(world instanceof ServerLevel serverLevel) {
            LevelRiftThemeData riftThemeData = LevelRiftThemeData.getFromLevel(serverLevel);
            var result = ((riftThemeData.getTheme() != null)?
                    riftThemeData.getTheme().value().getProcessors(themePieceType)
                    : defaultThemeProcessors(serverLevel, structurePos)).stream().filter(it->it instanceof RiftTemplateProcessor).map(it -> (RiftTemplateProcessor)it).toList();
            lastThemeTemplateProcessorCache = new Pair<>(new PhantomReference<>(world,null),result);
            return result;
        }
        return new ArrayList<>();
    }

    private List<StructureProcessor> defaultThemeProcessors(ServerLevel world, BlockPos structurePos) {
        Optional<Registry<RiftTheme>> registryReference = world.registryAccess().lookup(ModRiftThemes.RIFT_THEME_KEY);
        return registryReference.get()
                .get(ResourceLocation.fromNamespaceAndPath(WanderersOfTheRift.MODID, "cave"))
                .get()
                .value()
                .getProcessors(themePieceType);
    }

    protected StructureProcessorType<?> getType() {
        return RIFT_THEME.get();
    }

    @Override
    public BlockState processBlockState(BlockState currentState, int x, int y, int z, ServerLevelAccessor world, BlockPos structurePos, CompoundTag nbt, boolean isVisible) {
        var processors = getThemeTemplateProcessors(world.getLevel(), structurePos);

        for (int i = 0; i < processors.size() && currentState!=null; i++) {
            currentState = processors.get(i).processBlockState(currentState, x, y, z, world, structurePos, nbt, isVisible);
        }
        return currentState;

    }
}