package com.wanderersoftherift.wotr.world.level.levelgen.processor.theme;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.serialization.LaxRegistryCodec;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.RiftAdjacencyProcessor;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.RiftFinalProcessor;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.RiftTemplateProcessor;
import com.wanderersoftherift.wotr.world.level.levelgen.theme.RiftTheme;
import com.wanderersoftherift.wotr.world.level.levelgen.theme.ThemePieceType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;

import java.lang.ref.PhantomReference;
import java.util.ArrayList;
import java.util.List;

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
    public ThemeCache reloadCache(ServerLevel serverLevel, BlockPos structurePos, ThemePieceType themePieceType) {
        var newCache = new ThemeCache(new PhantomReference<>(serverLevel, null), new ArrayList<>(), new ArrayList<>(),
                new ArrayList<>());
        for (var processor : theme.value().getProcessors(themePieceType)) {
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
