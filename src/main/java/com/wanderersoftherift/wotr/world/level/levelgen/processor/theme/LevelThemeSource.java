package com.wanderersoftherift.wotr.world.level.levelgen.processor.theme;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.core.rift.RiftData;
import com.wanderersoftherift.wotr.world.level.levelgen.theme.ThemePieceType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;

import java.util.Collections;
import java.util.List;

public record LevelThemeSource() implements ThemeSource {
    public static final LevelThemeSource INSTANCE = new LevelThemeSource();
    public static final MapCodec<LevelThemeSource> CODEC = MapCodec.unit(INSTANCE);

    public List<StructureProcessor> getThemeProcessors(
            ServerLevel serverLevel,
            BlockPos structurePos,
            ThemePieceType themePieceType) {
        if (serverLevel == null) {
            return Collections.emptyList();
        }
        var riftData = RiftData.get(serverLevel);
        if (riftData.getTheme().isPresent()) {
            return riftData.getTheme().get().value().getProcessors(themePieceType);
        }
        return ThemeSource.defaultThemeProcessors(serverLevel, themePieceType);
    }

    @Override
    public ThemeCache reloadCache(ServerLevel serverLevel, BlockPos structurePos, ThemePieceType themePieceType) {
        var riftThemeData = RiftData.get(serverLevel);
        var structureProcessors = riftThemeData.getTheme()
                .map(it -> it.value().getProcessors(themePieceType))
                .orElse(ThemeSource.defaultThemeProcessors(serverLevel, themePieceType));
        return ThemeCache.fromProcessors(serverLevel, structureProcessors);
    }

    @Override
    public MapCodec<? extends ThemeSource> codec() {
        return CODEC;
    }

}
