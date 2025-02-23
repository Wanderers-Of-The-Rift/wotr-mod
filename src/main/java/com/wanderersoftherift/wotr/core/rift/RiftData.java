package com.wanderersoftherift.wotr.core.rift;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RiftData extends SavedData {
    private final ResourceKey<Level> portalDimension;
    private final BlockPos portalPos;
    private final List<UUID> players;


    public static SavedData.Factory<RiftData> factory(ResourceKey<Level> portalDimension, BlockPos portalPos) {
        return new SavedData.Factory<>(() -> new RiftData(portalDimension, portalPos, List.of()), RiftData::load);
    }

    private RiftData(ResourceKey<Level> portalDimension, BlockPos portalPos, List<UUID> players) {
        this.portalDimension = Objects.requireNonNull(portalDimension);
        this.portalPos = Objects.requireNonNull(portalPos);
        this.players = new ArrayList<>(Objects.requireNonNull(players));
    }

    public static RiftData load(CompoundTag tag, HolderLookup.Provider registries) {
        ResourceLocation portalDimensionLocation = ResourceLocation.parse(tag.getString("PortalDimension"));
        ResourceKey<Level> portalDimension = ResourceKey.create(Registries.DIMENSION, portalDimensionLocation);
        List<UUID> players = new ArrayList<>();
        tag.getList("Players", Tag.TAG_STRING).forEach( player -> players.add(UUID.fromString(player.getAsString())));
        return new RiftData(portalDimension, BlockPos.of(tag.getLong("PortalPos")), players);
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putString("PortalDimension", this.portalDimension.location().toString());
        tag.putLong("PortalPos", this.portalPos.asLong());
        ListTag playerTag = new ListTag();
        this.players.forEach(player -> playerTag.add(StringTag.valueOf(player.toString())));
        tag.put("Players", playerTag);
        return tag;
    }

    public BlockPos getPortalPos() {
        return this.portalPos;
    }

    public ResourceKey<Level> getPortalDimension() {
        return this.portalDimension;
    }

    public List<UUID> getPlayers() {
        return Collections.unmodifiableList(this.players);
    }

    public void addPlayer(UUID player) {
        if (this.players.contains(player)) {
            return;
        }
        this.players.add(player);
        this.setDirty();
    }

    public void removePlayer(UUID player) {
        this.players.remove(player);
        this.setDirty();
    }

}
