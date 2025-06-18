package com.wanderersoftherift.wotr.world.level.levelgen.template;

import com.wanderersoftherift.wotr.util.TripleMirror;
import com.wanderersoftherift.wotr.world.level.levelgen.RiftProcessedRoom;
import com.wanderersoftherift.wotr.world.level.levelgen.space.CorridorValidator;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.Collection;
import java.util.List;

/**
 * old way of placing bedrock around the room bedrock placement superseded by PerimeterGeneratable corridor placement
 * superseded by FastRiftGenerator.runCorridorBlender
 */

@Deprecated
public record LegacyPerimeterGeneratable(BlockState perimeterBlock, CorridorValidator validator)
        implements RiftGeneratable {

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
            var corridors = destination.space.corridors()
                    .stream()
                    .filter((corridor) -> corridor.position().getX() + spaceOrigin.getX() == destinationChunk.origin
                            .getX() && corridor.position().getZ() + spaceOrigin.getZ() == destinationChunk.origin.getZ()
                            && corridor.position().getY() + spaceOrigin.getY() == level)
                    .filter(riftSpaceCorridor -> validator.validateCorridor(
                            riftSpaceCorridor.position().getX() + spaceOrigin.getX(),
                            riftSpaceCorridor.position().getY() + spaceOrigin.getY(),
                            riftSpaceCorridor.position().getZ() + spaceOrigin.getZ(), riftSpaceCorridor.direction()
                    )
                    )
                    .toList();
            var hasCorridorNorth = corridors.stream().anyMatch((it) -> it.direction() == Direction.NORTH);
            var hasCorridorWest = corridors.stream().anyMatch((it) -> it.direction() == Direction.WEST);
            for (int y = level * 16; y < level * 16 + 16; y++) {
                for (int x = 0; x < 16 && originRelativeZ <= 0; x++) {
                    if (!hasCorridorNorth || x <= 6 || x >= 10 || y <= 5 + level * 16 || y >= 11 + level * 16) {
                        destinationChunk.setBlockState(x, y, 0, perimeterBlock);
                    }
                }
                for (int z = 0; z < 16 && originRelativeX <= 0; z++) {
                    if (!hasCorridorWest || z <= 6 || z >= 10 || y <= 5 + level * 16 || y >= 11 + level * 16) {
                        destinationChunk.setBlockState(0, y, z, perimeterBlock);
                    }
                }
                for (int xz = 0; xz < 256 && originRelativeY <= 0 && y <= level * 16; xz++) {
                    var z = (xz >>> 4) & 0xf;
                    var x = xz & 0xf;
                    destinationChunk.setBlockState(x, y, z, perimeterBlock);
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
        return "wotr:builtin:perimeter_old";
    }
}
