package com.wanderersoftherift.wotr.world.level.levelgen.template;

import com.wanderersoftherift.wotr.util.TripleMirror;
import com.wanderersoftherift.wotr.world.level.levelgen.RiftProcessedRoom;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.Collection;
import java.util.List;

public record SingleBlockChunkGeneratable(BlockState block) implements RiftGeneratable {

    @Override
    public void processAndPlace(
            RiftProcessedRoom destination,
            ServerLevelAccessor world,
            Vec3i placementShift,
            TripleMirror mirror) {
        var destinationChunk = destination.getOrCreateChunk(destination.space.origin());
        for (int i = 0; i < 4096; i++) {
            destinationChunk.blocks[i] = block;
        }
    }

    @Override
    public Collection<StructureTemplate.JigsawBlockInfo> jigsaws() {
        return List.of();
    }

    @Override
    public Vec3i size() {
        return new Vec3i(16, 16, 16);
    }

    @Override
    public String identifier() {
        return "wotr:builtin:single_block";
    }
}
