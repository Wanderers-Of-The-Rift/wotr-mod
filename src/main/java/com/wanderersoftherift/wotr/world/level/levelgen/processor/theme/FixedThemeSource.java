package com.wanderersoftherift.wotr.world.level.levelgen.processor.theme;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.serialization.LaxRegistryCodec;
import com.wanderersoftherift.wotr.world.level.levelgen.theme.RiftTheme;
import com.wanderersoftherift.wotr.world.level.levelgen.theme.ThemePieceType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;

import java.util.List;
import java.util.Optional;

public record FixedThemeSource(Holder<RiftTheme> theme) implements ThemeSource {
    public static final MapCodec<FixedThemeSource> CODEC = LaxRegistryCodec
            .refOrDirect(WotrRegistries.Keys.RIFT_THEMES, RiftTheme.DIRECT_CODEC)
            .xmap(FixedThemeSource::new, FixedThemeSource::theme)
            .fieldOf("theme");

    @Override
    public List<StructureProcessor> getThemeProcessors(
            ServerLevel world,
            BlockPos structurePos,
            ThemePieceType themePieceType) {
        return theme.value().getProcessors(themePieceType);
    }

    @Override
    public Optional<Holder<RiftTheme>> getTheme(ServerLevel level) {
        return Optional.of(theme);
    }

    @Override
    public ThemeCache reloadCache(ServerLevel serverLevel, BlockPos structurePos, ThemePieceType themePieceType) {
        return ThemeCache.fromProcessors(serverLevel, theme.value().getProcessors(themePieceType));
    }

    @Override
    public MapCodec<? extends ThemeSource> codec() {
        return CODEC;
    }
}
