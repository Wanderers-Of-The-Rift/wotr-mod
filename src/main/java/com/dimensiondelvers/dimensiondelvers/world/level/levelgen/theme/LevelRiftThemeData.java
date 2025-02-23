package com.dimensiondelvers.dimensiondelvers.world.level.levelgen.theme;

import com.dimensiondelvers.dimensiondelvers.DimensionDelvers;
import com.dimensiondelvers.dimensiondelvers.init.ModRiftThemes;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.Optional;

public class LevelRiftThemeData extends SavedData {

    private RiftTheme theme;

    public static LevelRiftThemeData getFromLevel(ServerLevel level) {
        LevelRiftThemeData riftTheme = level.getServer().overworld().getDataStorage().computeIfAbsent(new Factory<>(LevelRiftThemeData::create, LevelRiftThemeData::load), "rift_theme");
        level.registryAccess().get(ModRiftThemes.RIFT_THEME_KEY).ifPresent(holder -> {
            Optional<Holder.Reference<RiftTheme>> theme = holder.value().get(ResourceLocation.fromNamespaceAndPath(DimensionDelvers.MODID, "forest"));
            theme.ifPresent(riftThemeReference -> riftTheme.setTheme(riftThemeReference.value()));
        });
        return level.getDataStorage().computeIfAbsent(new Factory<>(LevelRiftThemeData::create, LevelRiftThemeData::load), "rift_theme");
    }

    public static LevelRiftThemeData create() {
        return new LevelRiftThemeData();
    }

    public static LevelRiftThemeData load(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        ResourceLocation themeRL = ResourceLocation.parse(tag.getString("theme"));
        LevelRiftThemeData data = create();
        lookupProvider.get(ModRiftThemes.RIFT_THEME_KEY).ifPresent(holder -> {
            Optional<Holder.Reference<RiftTheme>> theme = holder.value().get(themeRL);
            theme.ifPresent(riftThemeReference -> data.theme = riftThemeReference.value());
        });
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        ResourceLocation key = registries.get(ModRiftThemes.RIFT_THEME_KEY).get().value().getKey(theme);
        if(key != null) {
            tag.putString("theme", key.toString());
        }
        return tag;
    }

    public RiftTheme getTheme() {
        return theme;
    }

    public void setTheme(RiftTheme theme) {
        this.theme = theme;
        this.setDirty();
    }
}
