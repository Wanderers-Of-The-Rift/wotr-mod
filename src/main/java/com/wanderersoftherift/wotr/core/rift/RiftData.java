package com.wanderersoftherift.wotr.core.rift;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.item.riftkey.RiftConfig;
import com.wanderersoftherift.wotr.rift.objective.OngoingObjective;
import com.wanderersoftherift.wotr.world.level.levelgen.theme.RiftTheme;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceKey;
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
public class RiftData extends SavedData {
    public static final MapCodec<RiftData> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceKey.codec(Registries.DIMENSION).fieldOf("PortalDimension").forGetter(RiftData::getPortalDimension),
            BlockPos.CODEC.fieldOf("PortalPos").forGetter(RiftData::getPortalPos),
            UUIDUtil.STRING_CODEC.listOf().fieldOf("Players").forGetter(RiftData::getPlayers),
            UUIDUtil.STRING_CODEC.listOf().fieldOf("BannedPlayers").forGetter(RiftData::getBannedPlayers),
            OngoingObjective.DIRECT_CODEC.optionalFieldOf("Objective").forGetter(RiftData::getObjective),
            RiftConfig.CODEC.fieldOf("Config").forGetter(RiftData::getConfig)
    ).apply(instance, RiftData::new));

    private ResourceKey<Level> portalDimension;
    private BlockPos portalPos;
    private final List<UUID> players;
    private final List<UUID> bannedPlayers;
    private Optional<OngoingObjective> objective;
    private RiftConfig config;

    private RiftData(ResourceKey<Level> portalDimension, BlockPos portalPos, List<UUID> players,
            List<UUID> bannedPlayers, Optional<OngoingObjective> objective, RiftConfig config) {
        this.portalDimension = Objects.requireNonNull(portalDimension);
        this.portalPos = Objects.requireNonNull(portalPos);
        this.players = new ArrayList<>(Objects.requireNonNull(players));
        this.bannedPlayers = new ArrayList<>(Objects.requireNonNull(bannedPlayers));
        this.objective = objective;
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
        return CODEC
                .decode(registries.createSerializationContext(NbtOps.INSTANCE),
                        NbtOps.INSTANCE.getMap(tag).getOrThrow())
                .getOrThrow();
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        var result = CODEC.encode(this, registries.createSerializationContext(NbtOps.INSTANCE),
                NbtOps.INSTANCE.mapBuilder());
        return (CompoundTag) result.build(tag).getOrThrow();
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
        return config.theme();
    }

    public void setObjective(Optional<OngoingObjective> objective) {
        this.objective = objective;
        this.setDirty();
    }

    public void setObjective(OngoingObjective objective) {
        setObjective(Optional.of(objective));
    }

    public boolean isRiftEmpty() {
        return players.isEmpty();
    }

    public Optional<OngoingObjective> getObjective() {
        return objective;
    }
}
