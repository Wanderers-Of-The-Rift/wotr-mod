package com.wanderersoftherift.wotr.world.level.levelgen.processor.theme;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.util.Ref;
import com.wanderersoftherift.wotr.world.level.levelgen.RiftProcessedRoom;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.RiftAdjacencyProcessor;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.RiftFinalProcessor;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.RiftTemplateProcessor;
import com.wanderersoftherift.wotr.world.level.levelgen.theme.ThemePieceType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import javax.annotation.Nullable;
import java.util.List;

import static com.wanderersoftherift.wotr.init.worldgen.WotrProcessors.RIFT_THEME;

public class ThemeProcessor extends StructureProcessor
        implements RiftTemplateProcessor, RiftFinalProcessor, RiftAdjacencyProcessor<ThemeAdjacencyData> {
    public static final MapCodec<ThemeProcessor> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            ThemePieceType.CODEC.fieldOf("piece_type").forGetter(ThemeProcessor::getThemePieceType),
            ThemeSource.CODEC.optionalFieldOf("theme_source", LevelThemeSource.INSTANCE)
                    .forGetter(ThemeProcessor::getThemeSource)
    ).apply(builder, ThemeProcessor::new));

    private final ThemePieceType themePieceType;
    private final ThemeSource themeSource;
    private ThemeCache lastThemeTemplateProcessorCache = null;

    public ThemeProcessor(ThemePieceType themePieceType, ThemeSource themeSource) {
        this.themePieceType = themePieceType;
        this.themeSource = themeSource;
    }

    public ThemeSource getThemeSource() {
        return themeSource;
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
        if (!(world instanceof ServerLevelAccessor sa)) {
            return blockInfo;
        }
        List<StructureProcessor> processors = themeSource.getThemeProcessors(sa.getLevel(), structurePos,
                themePieceType);

        for (int i = 0; i < processors.size() && blockInfo != null; i++) {
            blockInfo = processors.get(i)
                    .process(world, piecePos, structurePos, rawBlockInfo, blockInfo, settings, template);
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

        for (StructureProcessor structureprocessor : themeSource.getThemeProcessors(serverLevel.getLevel(),
                structurePos, themePieceType)) {
            result = structureprocessor.finalizeProcessing(serverLevel, piecePos, structurePos, originalBlockInfos,
                    result, settings);
        }

        return result;
    }

    private ThemeCache reloadCache(ServerLevel world, BlockPos structurePos) {
        var currentCache = lastThemeTemplateProcessorCache;
        if (world != null && currentCache != null && currentCache.level().refersTo(world)) {
            return currentCache;
        }
        if (world instanceof ServerLevel serverLevel) {
            return (lastThemeTemplateProcessorCache = themeSource.reloadCache(serverLevel, structurePos,
                    themePieceType));
        }
        return ThemeCache.EMPTY;
    }

    private List<RiftTemplateProcessor> getThemeTemplateProcessors(ServerLevel world, BlockPos structurePos) {
        return reloadCache(world, structurePos).templateProcessors();
    }

    private List<RiftFinalProcessor> getFinalTemplateProcessors(ServerLevel world, BlockPos structurePos) {
        return reloadCache(world, structurePos).finalProcessors();
    }

    private List<RiftAdjacencyProcessor<?>> getRiftAdjacencyProcessors(ServerLevel world, BlockPos structurePos) {
        return reloadCache(world, structurePos).adjacencyProcessors();
    }

    protected StructureProcessorType<?> getType() {
        return RIFT_THEME.get();
    }

    @Override
    public BlockState processBlockState(
            BlockState currentState,
            int x,
            int y,
            int z,
            ServerLevelAccessor world,
            BlockPos structurePos,
            Ref<BlockEntity> entityRef,
            boolean isVisible) {
        var processors = getThemeTemplateProcessors(world.getLevel(), structurePos);

        for (int i = 0; i < processors.size() && currentState != null; i++) {
            currentState = processors.get(i)
                    .processBlockState(currentState, x, y, z, world, structurePos, entityRef, isVisible);
        }
        return currentState;

    }

    @Override
    public void finalizeRoomProcessing(
            RiftProcessedRoom room,
            ServerLevelAccessor world,
            BlockPos structurePos,
            Vec3i pieceSize) {

        var processors2 = getFinalTemplateProcessors(world.getLevel(), structurePos);

        for (int i = 0; i < processors2.size(); i++) {
            processors2.get(i).finalizeRoomProcessing(room, world, structurePos, pieceSize);
        }
    }

    @Override
    public int processAdjacency(ThemeAdjacencyData data, BlockState[] adjacentBlocks, boolean isHidden) {
        var i = 0;
        List<? extends ProcessorDataPair<?>> list = data.list();
        for (int j = 0; j < list.size(); j++) {
            i |= list.get(j).run(adjacentBlocks, isHidden);
        }
        return i;
    }

    @Override
    public ThemeAdjacencyData createData(BlockPos structurePos, Vec3i pieceSize, ServerLevelAccessor world) {
        return new ThemeAdjacencyData(getRiftAdjacencyProcessors(world.getLevel(), structurePos).stream()
                .map((it) -> RiftAdjacencyProcessor.ProcessorDataPair.create(it, structurePos, pieceSize, world))
                .toList());
    }

}