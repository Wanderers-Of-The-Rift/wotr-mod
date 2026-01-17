package com.wanderersoftherift.wotr.world.level.levelgen.processor.theme;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.world.level.levelgen.theme.RiftTheme;
import com.wanderersoftherift.wotr.world.level.levelgen.theme.ThemePieceType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public interface ThemeSource {

    Codec<ThemeSource> CODEC = WotrRegistries.THEME_SOURCE_TYPE.byNameCodec()
            .dispatch(ThemeSource::codec, Function.identity());

    static List<StructureProcessor> defaultThemeProcessors(ServerLevel world, ThemePieceType themePieceType) {
        Optional<Registry<RiftTheme>> registryReference = world.registryAccess()
                .lookup(WotrRegistries.Keys.RIFT_THEMES);
        return registryReference.get()
                .get(ResourceLocation.fromNamespaceAndPath(WanderersOfTheRift.MODID, "cave"))
                .get()
                .value()
                .getProcessors(themePieceType);
    }

    List<StructureProcessor> getThemeProcessors(
            ServerLevel world,
            BlockPos structurePos,
            ThemePieceType themePieceType);

    @Nullable Holder<Biome> getThemeBiome(ServerLevel serverLevel);

    ThemeCache reloadCache(ServerLevel serverLevel, BlockPos structurePos, ThemePieceType themePieceType);

    MapCodec<? extends ThemeSource> codec();

}
