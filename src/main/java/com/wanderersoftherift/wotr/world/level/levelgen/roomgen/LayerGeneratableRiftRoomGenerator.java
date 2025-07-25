package com.wanderersoftherift.wotr.world.level.levelgen.roomgen;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.item.riftkey.RiftConfig;
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

    public record Factory(RiftGeneratable generatable, RiftRoomGenerator.Factory baseFactory)
            implements RiftRoomGenerator.Factory {
        public static final MapCodec<Factory> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                RiftGeneratable.BUILTIN_GENERATABLE_CODEC.fieldOf("layered_generatable")
                        .forGetter(Factory::generatable),
                RiftRoomGenerator.Factory.CODEC.fieldOf("base").forGetter(Factory::baseFactory)
        ).apply(instance, Factory::new));

        @Override
        public RiftRoomGenerator create(RiftConfig config) {
            return new LayerGeneratableRiftRoomGenerator(generatable, baseFactory.create(config));
        }

        @Override
        public MapCodec<? extends RiftRoomGenerator.Factory> codec() {
            return CODEC;
        }
    }
}
