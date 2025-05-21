package com.wanderersoftherift.wotr.world.level.levelgen.template;

import com.wanderersoftherift.wotr.util.TripleMirror;
import com.wanderersoftherift.wotr.world.level.levelgen.RiftProcessedRoom;
import com.wanderersoftherift.wotr.world.level.levelgen.space.CorridorValidator;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.Collection;
import java.util.List;

public record PerimeterGeneratable(BlockState perimeterBlock, CorridorValidator validator) implements RiftGeneratable {

    @Override
    public void processAndPlace(
            RiftProcessedRoom destination,
            ServerLevelAccessor world,
            Vec3i placementShift,
            TripleMirror mirror) {
        var spaceOrigin = destination.space.origin();
        for (var destinationChunk : destination.getOrCreateAllChunks()) {
            var level = destinationChunk.origin.getY();
            var originRelativeX = destinationChunk.origin.getX() - spaceOrigin.getX();
            var originRelativeY = level - spaceOrigin.getY();
            var originRelativeZ = destinationChunk.origin.getZ() - spaceOrigin.getZ();
            var levelBlock = level << 4;
            if (originRelativeX == 0) {
                for (int y = levelBlock; y < levelBlock + 16; y++) {
                    for (int z = 0; z < 16; z++) {
                        destinationChunk.setBlockState(0, y, z, perimeterBlock);
                    }
                }
            }
            if (originRelativeZ == 0) {
                for (int y = levelBlock; y < levelBlock + 16; y++) {
                    for (int x = 0; x < 16; x++) {
                        destinationChunk.setBlockState(x, y, 0, perimeterBlock);
                    }
                }
            }
            if (originRelativeY == 0) {
                for (int xz = 0; xz < 256; xz++) {
                    var z = (xz >>> 4) & 0xf;
                    var x = xz & 0xf;
                    destinationChunk.setBlockState(x, levelBlock, z, perimeterBlock);
                }
            }
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
        return "wotr:builtin:perimeter";
    }
}
