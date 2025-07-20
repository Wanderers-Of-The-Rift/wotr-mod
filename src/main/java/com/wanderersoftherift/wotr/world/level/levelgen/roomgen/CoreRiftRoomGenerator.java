package com.wanderersoftherift.wotr.world.level.levelgen.roomgen;

import com.wanderersoftherift.wotr.util.TripleMirror;
import com.wanderersoftherift.wotr.world.level.levelgen.RiftProcessedChunk;
import com.wanderersoftherift.wotr.world.level.levelgen.RiftProcessedRoom;
import com.wanderersoftherift.wotr.world.level.levelgen.jigsaw.JigsawListProcessor;
import com.wanderersoftherift.wotr.world.level.levelgen.space.RoomRiftSpace;
import com.wanderersoftherift.wotr.world.level.levelgen.template.RiftGeneratable;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public record CoreRiftRoomGenerator(PositionalRandomFactory randomFactory, List<JigsawListProcessor> jigsawProcessors)
        implements RiftRoomGenerator {
    @Override
    public CompletableFuture<RiftProcessedRoom> getOrCreateFutureProcessedRoom(
            RoomRiftSpace space,
            ServerLevelAccessor world) {
        var processedRoom2 = new RiftProcessedRoom(space);
        var origin = processedRoom2.space.origin();
        var randomSource = randomFactory.at(origin.getX(), origin.getY(), origin.getZ());
        var mirror = space.templateTransform();
        if (mirror == null) {
            mirror = TripleMirror.random(randomSource);
        }
        var template = space.template();
        if (template == null) {
            throw new IllegalStateException("template should not be null");
        }
        var border = new Vec3i(
                LevelChunkSection.SECTION_WIDTH - 1
                        - ((template.size().getX() - 1) & RiftProcessedChunk.CHUNK_WIDTH_MASK),
                LevelChunkSection.SECTION_HEIGHT - 1
                        - ((template.size().getY() - 1) & RiftProcessedChunk.CHUNK_HEIGHT_MASK),
                LevelChunkSection.SECTION_WIDTH - 1
                        - ((template.size().getZ() - 1) & RiftProcessedChunk.CHUNK_WIDTH_MASK)
        );
        RiftGeneratable.generate(template, processedRoom2, world, border, mirror, world.getServer(), randomSource, null,
                jigsawProcessors);
        processedRoom2.markAsComplete();
        return CompletableFuture.completedFuture(processedRoom2);
    }
}
