package com.wanderersoftherift.wotr.core.rift;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.inventory.snapshot.InventorySnapshot;
import com.wanderersoftherift.wotr.core.inventory.snapshot.InventorySnapshotSystem;
import com.wanderersoftherift.wotr.core.rift.stats.StatSnapshot;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public record RiftEntryState(InventorySnapshot entranceInventory, ResourceKey<Level> previousDimension,
        ResourceKey<Level> riftDimension, Vec3 previousPosition, StatSnapshot statSnapshot) {

    public static final RiftEntryState EMPTY = new RiftEntryState(new InventorySnapshot(),
            ResourceKey.create(Registries.DIMENSION, WanderersOfTheRift.id("empty")),
            ResourceKey.create(Registries.DIMENSION, WanderersOfTheRift.id("empty")), new Vec3(0, 0, 0),
            new StatSnapshot());

    public static final Codec<RiftEntryState> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            InventorySnapshot.CODEC.fieldOf("entrance_inventory").forGetter(RiftEntryState::entranceInventory),
            ResourceKey.codec(Registries.DIMENSION)
                    .fieldOf("previous_dimension")
                    .forGetter(RiftEntryState::previousDimension),
            ResourceKey.codec(Registries.DIMENSION).fieldOf("rift_dimension").forGetter(RiftEntryState::riftDimension),
            Vec3.CODEC.fieldOf("previous_position").forGetter(RiftEntryState::previousPosition),
            StatSnapshot.CODEC.fieldOf("stats").forGetter(RiftEntryState::statSnapshot)
    ).apply(ins, RiftEntryState::new));

    public static void pushEntryState(ServerPlayer player, ResourceKey<Level> riftDimension) {
        var currentEntryStates = player.getData(WotrAttachments.RIFT_ENTRY_STATES);
        var newEntryState = new RiftEntryState(
                InventorySnapshotSystem.captureSnapshot(player,
                        currentEntryStates.stream().map(it -> it.entranceInventory).toList()),
                player.level().dimension(), riftDimension, player.position(), new StatSnapshot(player));
        currentEntryStates.add(newEntryState);
    }
}
