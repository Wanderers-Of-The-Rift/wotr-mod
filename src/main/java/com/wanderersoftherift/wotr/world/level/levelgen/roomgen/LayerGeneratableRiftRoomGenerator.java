package com.wanderersoftherift.wotr.world.level.levelgen.roomgen;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.util.TripleMirror;
import com.wanderersoftherift.wotr.world.level.levelgen.RiftProcessedRoom;
import com.wanderersoftherift.wotr.world.level.levelgen.space.RoomRiftSpace;
import com.wanderersoftherift.wotr.world.level.levelgen.template.RiftGeneratable;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;

import java.util.concurrent.CompletableFuture;

public record LayerGeneratableRiftRoomGenerator(RiftGeneratable generatable, RiftRoomGenerator baseGenerator)
        implements RiftRoomGenerator {
    public static final MapCodec<LayerGeneratableRiftRoomGenerator> CODEC = RecordCodecBuilder
            .mapCodec(instance -> instance.group(
                    RiftGeneratable.BUILTIN_GENERATABLE_CODEC.fieldOf("generatable")
                            .forGetter(LayerGeneratableRiftRoomGenerator::generatable),
                    RiftRoomGenerator.CODEC.fieldOf("base_room_generator")
                            .forGetter(LayerGeneratableRiftRoomGenerator::baseGenerator))
                    .apply(instance, LayerGeneratableRiftRoomGenerator::new));

    @Override
    public MapCodec<? extends RiftRoomGenerator> codec() {
        return CODEC;
    }

    @Override
    public CompletableFuture<RiftProcessedRoom> getOrCreateFutureProcessedRoom(
            RoomRiftSpace space,
            ServerLevelAccessor world,
            PositionalRandomFactory randomFactory) {
        return baseGenerator.getOrCreateFutureProcessedRoom(space, world, randomFactory).thenApply(room -> {

            var origin = space.origin();
            var randomSource = randomFactory.at(origin.getX(), origin.getY(), origin.getZ());
            var mirror = space.templateTransform();
            if (mirror == null) {
                mirror = TripleMirror.random(randomSource);
            }
            generatable.processAndPlace(room, world, Vec3i.ZERO, mirror);
            return room;
        });
    }

    @Override
    public RiftRoomGenerator copyIfNeeded() {
        var newChild = baseGenerator.copyIfNeeded();
        if (newChild == baseGenerator) {
            return this;
        } else {
            return new LayerGeneratableRiftRoomGenerator(generatable, newChild);
        }
    }

}
