package com.wanderersoftherift.wotr.world.level.levelgen.processor.theme;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.RiftAdjacencyProcessor;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.RiftFinalProcessor;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.RiftTemplateProcessor;
import com.wanderersoftherift.wotr.world.level.levelgen.theme.ThemePieceType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;

import java.lang.ref.PhantomReference;
import java.util.ArrayList;
import java.util.List;

public record ThemeSourceFromRiftData() implements ThemeSource {
    public static final com.wanderersoftherift.wotr.world.level.levelgen.processor.theme.ThemeSourceFromRiftData INSTANCE = new com.wanderersoftherift.wotr.world.level.levelgen.processor.theme.ThemeSourceFromRiftData();
    public static final MapCodec<com.wanderersoftherift.wotr.world.level.levelgen.processor.theme.ThemeSourceFromRiftData> CODEC = MapCodec
            .unit(INSTANCE);

    public List<StructureProcessor> getThemeProcessors(
            ServerLevel world,
            BlockPos structurePos,
            ThemePieceType themePieceType) {
        if (world instanceof ServerLevel serverLevel) {
            var riftData = com.wanderersoftherift.wotr.core.rift.RiftData.get(serverLevel);
            if (riftData.getTheme().isPresent()) {
                return riftData.getTheme().get().value().getProcessors(themePieceType);
            }
            return ThemeSource.defaultThemeProcessors(serverLevel, themePieceType);
        }
        return new ArrayList<>();
    }

    @Override
    public ThemeCache reloadCache(ServerLevel serverLevel, BlockPos structurePos, ThemePieceType themePieceType) {
        var riftThemeData = com.wanderersoftherift.wotr.core.rift.RiftData.get(serverLevel);
        var structureProcessors = riftThemeData.getTheme()
                .map(it -> it.value().getProcessors(themePieceType))
                .orElse(ThemeSource.defaultThemeProcessors(serverLevel, themePieceType));
        var newCache = new ThemeCache(new PhantomReference<>(serverLevel, null), new ArrayList<>(), new ArrayList<>(),
                new ArrayList<>());
        for (var processor : structureProcessors) {
            var used = false;
            if (processor instanceof RiftTemplateProcessor riftTemplateProcessor) {
                newCache.templateProcessors().add(riftTemplateProcessor);
                used = true;
            }
            if (processor instanceof RiftFinalProcessor riftTemplateProcessor) {
                newCache.finalProcessors().add(riftTemplateProcessor);
                used = true;
            }
            if (processor instanceof RiftAdjacencyProcessor<?> replaceThisOrAdjacentRiftProcessor) {
                newCache.adjacencyProcessors().add(replaceThisOrAdjacentRiftProcessor);
                used = true;
            }
            if (!used) {
                WanderersOfTheRift.LOGGER.warn("incompatible processor type: {}", processor.getClass());
            }
        }
        return newCache;
    }

    @Override
    public MapCodec<? extends ThemeSource> codec() {
        return CODEC;
    }

}
