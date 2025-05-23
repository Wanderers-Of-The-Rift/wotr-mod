package com.wanderersoftherift.wotr.world.level.levelgen.theme;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.init.WotrTags;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

import static com.wanderersoftherift.wotr.WanderersOfTheRift.LOGGER;

public class LevelRiftThemeData extends SavedData {

    public static final Codec<LevelRiftThemeData> CODEC = RecordCodecBuilder
            .create(inst -> inst.group(RiftTheme.CODEC.optionalFieldOf("theme").forGetter(LevelRiftThemeData::getTheme))
                    .apply(inst, LevelRiftThemeData::new));

    private Optional<Holder<RiftTheme>> theme;

    public LevelRiftThemeData(Optional<Holder<RiftTheme>> theme) {
        this.theme = theme;
    }

    public static LevelRiftThemeData getFromLevel(ServerLevel level) {
        return level.getDataStorage()
                .computeIfAbsent(new Factory<>(LevelRiftThemeData::create, LevelRiftThemeData::load), "rift_theme");
    }

    public static LevelRiftThemeData create() {
        return new LevelRiftThemeData(Optional.empty());
    }

    public static LevelRiftThemeData load(CompoundTag tag, HolderLookup.Provider registries) {
        return CODEC.parse(registries.createSerializationContext(NbtOps.INSTANCE), tag.get("theme"))
                .resultOrPartial(LOGGER::error)
                .orElse(new LevelRiftThemeData(Optional.empty()));
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        CODEC.encodeStart(registries.createSerializationContext(NbtOps.INSTANCE), this)
                .resultOrPartial(LOGGER::error)
                .ifPresent(compound -> tag.put("theme", compound));
        return tag;
    }

    public Optional<Holder<RiftTheme>> getTheme() {
        return theme;
    }

    public void setTheme(Holder<RiftTheme> theme) {
        this.theme = Optional.of(theme);
        this.setDirty();
    }

    public void setTheme(Optional<Holder<RiftTheme>> theme) {
        this.theme = theme;
        this.setDirty();
    }

    public static Holder<RiftTheme> getRandomTheme(ServerLevel level) {
        Registry<RiftTheme> registry = level.registryAccess().lookupOrThrow(WotrRegistries.Keys.RIFT_THEMES);

        return registry.getRandomElementOf(WotrTags.RiftThemes.RANDOM_SELECTABLE, level.getRandom())
                .orElseThrow(() -> new IllegalStateException("No rift themes available"));
    }
}
