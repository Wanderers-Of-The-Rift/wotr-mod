package com.wanderersoftherift.wotr.world.level.levelgen.processor;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.rift.RiftData;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.util.Ref;
import com.wanderersoftherift.wotr.world.level.levelgen.RiftProcessedRoom;
import com.wanderersoftherift.wotr.world.level.levelgen.theme.RiftTheme;
import com.wanderersoftherift.wotr.world.level.levelgen.theme.ThemePieceType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
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
import java.lang.ref.PhantomReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.wanderersoftherift.wotr.init.worldgen.WotrProcessors.RIFT_THEME;

public class ThemeProcessor extends StructureProcessor implements RiftTemplateProcessor, RiftFinalProcessor,
        RiftAdjacencyProcessor<ThemeProcessor.ThemeAdjacencyData> {
    public static final MapCodec<ThemeProcessor> CODEC = RecordCodecBuilder.mapCodec(builder -> builder
            .group(ThemePieceType.CODEC.fieldOf("piece_type").forGetter(ThemeProcessor::getThemePieceType)
            ).apply(builder, ThemeProcessor::new));

    private ThemePieceType themePieceType;
    private ThemeCache lastThemeTemplateProcessorCache = null;

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
        if (!(world instanceof ServerLevelAccessor sa)) {
            return blockInfo;
        }
        List<StructureProcessor> processors = getThemeProcessors(sa.getLevel(), structurePos);

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

        for (StructureProcessor structureprocessor : getThemeProcessors(serverLevel.getLevel(), structurePos)) {
            result = structureprocessor.finalizeProcessing(serverLevel, piecePos, structurePos, originalBlockInfos,
                    result, settings);
        }

        return result;
    }

    private List<StructureProcessor> getThemeProcessors(ServerLevel world, BlockPos structurePos) {
        if (world instanceof ServerLevel serverLevel) {
            RiftData riftData = RiftData.get(serverLevel);
            if (riftData.getTheme().isPresent()) {
                return riftData.getTheme().get().value().getProcessors(themePieceType);
            }
            return defaultThemeProcessors(serverLevel, structurePos);
        }
        return new ArrayList<>();
    }

    private List<RiftTemplateProcessor> getThemeTemplateProcessors(ServerLevel world, BlockPos structurePos) {
        var currentCache = lastThemeTemplateProcessorCache;
        if (world != null && currentCache != null && currentCache.level.refersTo(world)) {
            return currentCache.templateProcessors;
        }
        if (world instanceof ServerLevel serverLevel) {
            return reloadCache(serverLevel, structurePos).templateProcessors;
        }
        return new ArrayList<>();
    }

    private List<RiftFinalProcessor> getFinalTemplateProcessors(ServerLevel world, BlockPos structurePos) {
        var currentCache = lastThemeTemplateProcessorCache;
        if (world != null && currentCache != null && currentCache.level.refersTo(world)) {
            return currentCache.finalProcessors;
        }
        if (world instanceof ServerLevel serverLevel) {
            return reloadCache(serverLevel, structurePos).finalProcessors;
        }
        return new ArrayList<>();
    }

    private List<RiftAdjacencyProcessor<?>> getRiftAdjacencyProcessors(ServerLevel world, BlockPos structurePos) {
        var currentCache = lastThemeTemplateProcessorCache;
        if (world != null && currentCache != null && currentCache.level.refersTo(world)) {
            return currentCache.airReplaceProcessors;
        }
        if (world instanceof ServerLevel serverLevel) {
            return reloadCache(serverLevel, structurePos).airReplaceProcessors;
        }
        return new ArrayList<>();
    }

    private ThemeCache reloadCache(ServerLevel serverLevel, BlockPos structurePos) {
        var riftThemeData = RiftData.get(serverLevel);
        var structureProcessors = riftThemeData.getTheme()
                .map(it -> it.value().getProcessors(themePieceType))
                .orElse(defaultThemeProcessors(serverLevel, structurePos));
        var newCache = new ThemeCache(new PhantomReference<>(serverLevel, null), new ArrayList<>(), new ArrayList<>(),
                new ArrayList<>());
        for (var processor : structureProcessors) {
            var used = false;
            if (processor instanceof RiftTemplateProcessor riftTemplateProcessor) {
                newCache.templateProcessors.add(riftTemplateProcessor);
                used = true;
            }
            if (processor instanceof RiftFinalProcessor riftTemplateProcessor) {
                newCache.finalProcessors.add(riftTemplateProcessor);
                used = true;
            }
            if (processor instanceof RiftAdjacencyProcessor<?> replaceThisOrAdjacentRiftProcessor) {
                newCache.airReplaceProcessors.add(replaceThisOrAdjacentRiftProcessor);
                used = true;
            }
            if (!used) {
                WanderersOfTheRift.LOGGER.warn("incompatible processor type:" + processor.getClass());
            }
        }
        return lastThemeTemplateProcessorCache = newCache;

    }

    private List<StructureProcessor> defaultThemeProcessors(ServerLevel world, BlockPos structurePos) {
        Optional<Registry<RiftTheme>> registryReference = world.registryAccess()
                .lookup(WotrRegistries.Keys.RIFT_THEMES);
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
    public int processAdjacency(ThemeAdjacencyData data, BlockState[] asArray, boolean isHidden) {
        var i = 0;
        List<? extends ProcessorDataPair<?>> list = data.list;
        for (int j = 0; j < list.size(); j++) {
            i |= list.get(j).run(asArray, isHidden);
        }
        return i;
    }

    @Override
    public ThemeAdjacencyData createData(BlockPos structurePos, Vec3i pieceSize, ServerLevelAccessor world) {
        return new ThemeAdjacencyData(getRiftAdjacencyProcessors(world.getLevel(), structurePos).stream()
                .map((it) -> RiftAdjacencyProcessor.ProcessorDataPair.create(it, structurePos, pieceSize, world))
                .toList());
    }

    private record ThemeCache(PhantomReference<LevelReader> level, List<RiftTemplateProcessor> templateProcessors,
            List<RiftFinalProcessor> finalProcessors, List<RiftAdjacencyProcessor<?>> airReplaceProcessors) {
    }

    public record ThemeAdjacencyData(List<? extends ProcessorDataPair<?>> list) {
    }
}