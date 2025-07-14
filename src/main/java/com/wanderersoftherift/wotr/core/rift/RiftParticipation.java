package com.wanderersoftherift.wotr.core.rift;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.inventory.snapshot.InventorySnapshot;
import com.wanderersoftherift.wotr.core.inventory.snapshot.InventorySnapshotSystem;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public record RiftParticipation(InventorySnapshot entranceInventory, ResourceKey<Level> previousDimension,
        ResourceKey<Level> riftDimension, Vec3 previousPosition) {

    public static final RiftParticipation EMPTY = new RiftParticipation(new InventorySnapshot(),
            ResourceKey.create(Registries.DIMENSION, WanderersOfTheRift.id("empty")),
            ResourceKey.create(Registries.DIMENSION, WanderersOfTheRift.id("empty")), new Vec3(0, 0, 0));

    public static final Codec<RiftParticipation> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            InventorySnapshot.CODEC.fieldOf("entrance_inventory").forGetter(RiftParticipation::entranceInventory),
            ResourceKey.codec(Registries.DIMENSION)
                    .fieldOf("previous_dimension")
                    .forGetter(RiftParticipation::previousDimension),
            ResourceKey.codec(Registries.DIMENSION)
                    .fieldOf("rift_dimension")
                    .forGetter(RiftParticipation::riftDimension),
            Vec3.CODEC.fieldOf("previous_position").forGetter(RiftParticipation::previousPosition)
    ).apply(ins, RiftParticipation::new));

    public static void pushParticipation(ServerPlayer player, ResourceKey<Level> riftDimension) {
        var currentParticipations = player.getData(WotrAttachments.PARTICIPATIONS);
        var newParticipation = new RiftParticipation(
                InventorySnapshotSystem.captureSnapshot(player,
                        currentParticipations.stream().map(it -> it.entranceInventory).toList()),
                player.level().dimension(), riftDimension, player.position());
        currentParticipations.add(newParticipation);
    }
}
