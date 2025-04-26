package com.wanderersoftherift.wotr.world.level.levelgen.theme;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.ModRiftThemes;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.Optional;

import static com.wanderersoftherift.wotr.WanderersOfTheRift.LOGGER;

public class LevelRiftThemeData extends SavedData {

    public static final Codec<LevelRiftThemeData> CODEC = RecordCodecBuilder
            .create(inst -> inst.group(RiftTheme.CODEC.fieldOf("theme").forGetter(LevelRiftThemeData::getTheme))
                    .apply(inst, LevelRiftThemeData::new));

    private Holder<RiftTheme> theme;

    public LevelRiftThemeData(Holder<RiftTheme> theme) {
        this.theme = theme;
    }

    public static LevelRiftThemeData getFromLevel(ServerLevel level) {
        return level.getDataStorage()
                .computeIfAbsent(new Factory<>(LevelRiftThemeData::create, LevelRiftThemeData::load), "rift_theme");
    }

    public static LevelRiftThemeData create() {
        return new LevelRiftThemeData(null);
    }

    public static LevelRiftThemeData load(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        Holder<RiftTheme> theme = RiftTheme.CODEC.parse(NbtOps.INSTANCE, tag.get("theme"))
                .resultOrPartial(LOGGER::error)
                .orElse(null);
        return new LevelRiftThemeData(theme);
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        RiftTheme.CODEC.encodeStart(NbtOps.INSTANCE, this.getTheme())
                .resultOrPartial(LOGGER::error)
                .ifPresent(compound -> tag.put("theme", compound));
        return tag;
    }

    public Holder<RiftTheme> getTheme() {
        return theme;
    }

    public void setTheme(Holder<RiftTheme> theme) {
        this.theme = theme;
        this.setDirty();
    }

    public static Holder<RiftTheme> getRandomTheme(ServerLevel level) {
        Optional<Registry<RiftTheme>> registryReference = level.registryAccess().lookup(ModRiftThemes.RIFT_THEME_KEY);
        var riftTheme = registryReference.flatMap(x -> x.getRandom(level.getRandom())).orElse(null);
        if (riftTheme == null) {
            WanderersOfTheRift.LOGGER.error("Failed to get random rift theme");
        }
        return riftTheme;
    }
}
