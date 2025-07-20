package com.wanderersoftherift.wotr.world.level.levelgen.template;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.serialization.StringBlockStateCodec;
import com.wanderersoftherift.wotr.util.TripleMirror;
import com.wanderersoftherift.wotr.world.level.levelgen.RiftProcessedRoom;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.Collection;
import java.util.List;

/**
 * places bedrock around the room
 */
public record PerimeterGeneratable(BlockState perimeterBlock) implements RiftGeneratable {

    public static final MapCodec<PerimeterGeneratable> CODEC = RecordCodecBuilder
            .mapCodec(instance -> instance
                    .group(StringBlockStateCodec.INSTANCE.fieldOf("perimeter_block")
                            .forGetter(PerimeterGeneratable::perimeterBlock))
                    .apply(instance, PerimeterGeneratable::new));

    @Override
    public MapCodec<? extends RiftGeneratable> codec() {
        return CODEC;
    }

    @Override
    public void processAndPlace(
            RiftProcessedRoom destination,
            ServerLevelAccessor world,
            Vec3i placementShift,
            TripleMirror mirror) {
        var spaceOrigin = destination.space.origin();
        var template = destination.space.template();
        if (template == null) {
            return;
        }

        var border = new Vec3i(
                15 - ((template.size().getX() - 1) & 0xf), 15 - ((template.size().getY() - 1) & 0xf),
                15 - ((template.size().getZ() - 1) & 0xf)
        );

        for (var destinationChunk : destination.getOrCreateAllChunks()) {
            var level = destinationChunk.origin.getY();
            var originRelativeX = destinationChunk.origin.getX() - spaceOrigin.getX();
            var originRelativeY = level - spaceOrigin.getY();
            var originRelativeZ = destinationChunk.origin.getZ() - spaceOrigin.getZ();
            var levelBlock = level << 4;
            if (originRelativeX == 0) {
                for (int y = levelBlock; y < levelBlock + 16; y++) {
                    for (int z = 0; z < 16; z++) {
                        for (int x = 0; x < border.getZ(); x++) {
                            destinationChunk.setBlockState(x, y, z, perimeterBlock);
                        }
                    }
                }
            }
            if (originRelativeZ == 0) {
                for (int y = levelBlock; y < levelBlock + 16; y++) {
                    for (int x = 0; x < 16; x++) {
                        for (int z = 0; z < border.getZ(); z++) {
                            destinationChunk.setBlockState(x, y, z, perimeterBlock);
                        }
                    }
                }
            }
            if (originRelativeY == 0) {
                for (int xz = 0; xz < 256; xz++) {
                    var z = (xz >>> 4) & 0xf;
                    var x = xz & 0xf;
                    for (int y = 0; y < border.getZ(); y++) {
                        destinationChunk.setBlockState(x, levelBlock + y, z, perimeterBlock);
                    }
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
