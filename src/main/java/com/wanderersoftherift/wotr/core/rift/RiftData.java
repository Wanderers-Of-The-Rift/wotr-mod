package com.wanderersoftherift.wotr.core.rift;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.item.riftkey.RiftConfig;
import com.wanderersoftherift.wotr.world.level.levelgen.theme.RiftTheme;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RiftData extends SavedData { // TODO: split this
    private ResourceKey<Level> portalDimension;
    private BlockPos portalPos;
    private final List<UUID> players;
    private final List<UUID> bannedPlayers;
    private Optional<Holder<RiftTheme>> theme;
    private RiftConfig config;

    private RiftData(ResourceKey<Level> portalDimension, BlockPos portalPos, List<UUID> players,
            List<UUID> bannedPlayers, Optional<Holder<RiftTheme>> theme, RiftConfig config) {
        this.portalDimension = Objects.requireNonNull(portalDimension);
        this.portalPos = Objects.requireNonNull(portalPos);
        this.players = new ArrayList<>(Objects.requireNonNull(players));
        this.bannedPlayers = new ArrayList<>(Objects.requireNonNull(bannedPlayers));
        this.theme = theme;
        this.config = config;
    }

    public static RiftData get(ServerLevel level) {
        return level.getDataStorage()
                .computeIfAbsent(factory(level.getServer().overworld().dimension(),
                        level.getServer().overworld().getSharedSpawnPos(), new RiftConfig(0)), "rift_data");
    }

    private static SavedData.Factory<RiftData> factory(
            ResourceKey<Level> portalDimension,
            BlockPos portalPos,
            RiftConfig config) {
        return new SavedData.Factory<>(
                () -> new RiftData(portalDimension, portalPos, List.of(), List.of(), Optional.empty(), config),
                RiftData::load);
    }

    private static RiftData load(CompoundTag tag, HolderLookup.Provider registries) {
        ResourceLocation portalDimensionLocation = ResourceLocation.parse(tag.getString("PortalDimension"));
        ResourceKey<Level> portalDimension = ResourceKey.create(Registries.DIMENSION, portalDimensionLocation);
        List<UUID> players = new ArrayList<>();
        tag.getList("Players", Tag.TAG_STRING).forEach(player -> players.add(UUID.fromString(player.getAsString())));
        List<UUID> bannedPlayers = new ArrayList<>();
        tag.getList("BannedPlayers", Tag.TAG_STRING)
                .forEach(player -> bannedPlayers.add(UUID.fromString(player.getAsString())));
        RiftConfig config = new RiftConfig(0);
        if (tag.contains("Config")) {
            config = RiftConfig.CODEC.parse(registries.createSerializationContext(NbtOps.INSTANCE), tag.get("Config"))
                    .resultOrPartial(x -> WanderersOfTheRift.LOGGER.error("Tried to load invalid rift config: '{}'", x))
                    .orElse(new RiftConfig(0));
        }
        Optional<Holder<RiftTheme>> theme = Optional.empty();
        if (tag.contains("Theme")) {
            theme = RiftTheme.CODEC.parse(registries.createSerializationContext(NbtOps.INSTANCE), tag.get("Theme"))
                    .resultOrPartial(x -> WanderersOfTheRift.LOGGER.error("Tried to load invalid rift theme: '{}'", x));
        }
        return new RiftData(portalDimension, BlockPos.of(tag.getLong("PortalPos")), players, bannedPlayers, theme,
                config);
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putString("PortalDimension", this.portalDimension.location().toString());
        tag.putLong("PortalPos", this.portalPos.asLong());
        ListTag playerTag = new ListTag();
        this.players.forEach(player -> playerTag.add(StringTag.valueOf(player.toString())));
        tag.put("Players", playerTag);
        ListTag bannedPlayerTag = new ListTag();
        this.bannedPlayers.forEach(player -> bannedPlayerTag.add(StringTag.valueOf(player.toString())));
        tag.put("BannedPlayers", bannedPlayerTag);
        if (config != null) {
            tag.put("Config",
                    RiftConfig.CODEC.encodeStart(registries.createSerializationContext(NbtOps.INSTANCE), config)
                            .getOrThrow());
        }
        theme.ifPresent(riftThemeHolder -> tag.put("Theme",
                RiftTheme.CODEC.encodeStart(registries.createSerializationContext(NbtOps.INSTANCE), riftThemeHolder)
                        .getOrThrow()));
        return tag;
    }

    public BlockPos getPortalPos() {
        return this.portalPos;
    }

    public void setPortalPos(BlockPos portalPos) {
        this.portalPos = portalPos;
        this.setDirty();
    }

    public ResourceKey<Level> getPortalDimension() {
        return this.portalDimension;
    }

    public void setPortalDimension(ResourceKey<Level> portalDimension) {
        this.portalDimension = portalDimension;
        this.setDirty();
    }

    public List<UUID> getPlayers() {
        return Collections.unmodifiableList(this.players);
    }

    public List<UUID> getBannedPlayers() {
        return Collections.unmodifiableList(bannedPlayers);
    }

    public void addPlayer(UUID player) {
        if (this.players.contains(player)) {
            return;
        }
        this.players.add(player);
        this.setDirty();
    }

    public void removePlayer(ServerPlayer player) {
        this.players.remove(player.getUUID());
        this.bannedPlayers.add(player.getUUID());
        this.setDirty();
    }

    public RiftConfig getConfig() {
        return config;
    }

    public void setConfig(RiftConfig config) {
        this.config = config;
        this.setDirty();
    }

    public int getTier() {
        return config.tier();
    }

    public boolean containsPlayer(Player player) {
        return players.contains(player.getUUID());
    }

    public boolean isBannedFromRift(Player player) {
        return bannedPlayers.contains(player.getUUID());
    }

    public Optional<Holder<RiftTheme>> getTheme() {
        return theme;
    }

    public void setTheme(Optional<Holder<RiftTheme>> theme) {
        this.theme = theme;
        this.setDirty();
    }

    public void setTheme(Holder<RiftTheme> theme) {
        this.theme = Optional.of(theme);
        this.setDirty();
    }

    public boolean isRiftEmpty() {
        return players.isEmpty();
    }
}
