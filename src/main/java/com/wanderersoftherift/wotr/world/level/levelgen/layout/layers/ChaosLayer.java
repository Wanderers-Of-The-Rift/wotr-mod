package com.wanderersoftherift.wotr.world.level.levelgen.layout.layers;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.item.riftkey.RiftConfig;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.LayeredRiftLayout;
import com.wanderersoftherift.wotr.world.level.levelgen.space.RiftSpace;
import com.wanderersoftherift.wotr.world.level.levelgen.space.RiftSpaceCorridor;
import com.wanderersoftherift.wotr.world.level.levelgen.template.randomizers.RoomRandomizer;
import com.wanderersoftherift.wotr.world.level.levelgen.template.randomizers.RoomRandomizerImpl;
import it.unimi.dsi.fastutil.ints.IntImmutableList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.Vec3i;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.Collections;

public class ChaosLayer implements LayeredRiftLayout.LayoutLayer {

    private static final IntList MASKS;
    private final RoomRandomizer roomRandomizer;

    public ChaosLayer(RoomRandomizer roomRandomizer) {
        this.roomRandomizer = roomRandomizer;
    }

    private RiftSpace nextChaoticSpace(
            RiftSpaceCorridor corridor,
            RandomSource randomSource,
            Vec3i roomPosition,
            LayeredRiftLayout.LayoutSection section) {

        var slices = new int[] { sliceBitmap(corridor, 0, roomPosition, section),
                sliceBitmap(corridor, 1, roomPosition, section), sliceBitmap(corridor, 2, roomPosition, section) };
        if ((slices[0] & 0b00000_00000_00100_00000_00000) == 0) {
            return null;// corridor is blocked so no room can be placed here
        }
        for (int combination = 16 + randomSource.nextInt(240); combination >= 0; combination--) {
            var mask = MASKS.getInt(combination);
            if (mask < 0) {
                continue;
            }
            int depth = 0;
            for (; depth < 3 && ((slices[depth] & mask) == mask); depth++) {
            }
            if (depth == 0) {
                continue;
            }

            var position = combination & 0b1111;
            var size = combination >> 4;
            var x = (position & 3) - 2;
            var y = (position >> 2) - 2;
            var maxWidth = ((size & 0x1) | ((size & 0x4) >> 1)) + 1;
            var maxHeight = (((size & 0x2) >> 1) | ((size & 0x8) >> 2)) + 1;

            var tangentDirection = corridor.direction().getClockWise();
            var maxRoomSize = new Vec3i(
                    depth * Math.abs(corridor.direction().getStepX())
                            + maxWidth * Math.abs(tangentDirection.getStepX()),
                    maxHeight, depth * Math.abs(corridor.direction().getStepZ())
                            + maxWidth * Math.abs(tangentDirection.getStepZ()));

            var space = roomRandomizer.randomSpace(randomSource, maxRoomSize);
            var spaceOffsetX = roomPosition.getX() + corridor.position().getX();
            var spaceOffsetY = roomPosition.getY() + corridor.position().getY();
            var spaceOffsetZ = roomPosition.getZ() + corridor.position().getZ();
            var altYOffset = spaceOffsetY + 1 - (space.origin().getY() + space.size().getY());
            spaceOffsetY += y;
            switch (corridor.direction()) {
                case NORTH -> {
                    var altXOffset = spaceOffsetX + 1 - (space.origin().getX() + space.size().getX());
                    spaceOffsetX += x;
                    spaceOffsetZ -= space.size().getZ();
                    if (spaceOffsetX < altXOffset) {
                        spaceOffsetX = altXOffset;
                    }
                }
                case SOUTH -> {
                    var altXOffset = spaceOffsetX + 1 - (space.origin().getX() + space.size().getX());
                    spaceOffsetX -= maxWidth - 1 + x;
                    spaceOffsetZ += 1;
                    if (spaceOffsetX < altXOffset) {
                        spaceOffsetX = altXOffset;
                    }
                }
                case WEST -> {
                    var altZOffset = spaceOffsetZ + 1 - (space.origin().getZ() + space.size().getZ());
                    spaceOffsetZ -= x + maxWidth - 1;
                    spaceOffsetX -= space.size().getX();
                    if (spaceOffsetZ < altZOffset) {
                        spaceOffsetZ = altZOffset;
                    }
                }
                case EAST -> {
                    var altZOffset = spaceOffsetZ + 1 - (space.origin().getZ() + space.size().getZ());
                    spaceOffsetZ += x;
                    spaceOffsetX += 1;
                    if (spaceOffsetZ < altZOffset) {
                        spaceOffsetZ = altZOffset;
                    }
                }
            }

            if (spaceOffsetY < altYOffset) {
                spaceOffsetY = altYOffset;
            }

            var result = space.offset(spaceOffsetX, spaceOffsetY, spaceOffsetZ);
            return result;
        }
        return null;
    }

    private int sliceBitmap(
            RiftSpaceCorridor corridor,
            int offset,
            Vec3i roomPosition,
            LayeredRiftLayout.LayoutSection section) {

        var origin = section.sectionShape().getBoxStart();
        var size = section.sectionShape().getBoxSize();
        var emptySpaces = section.getEmptySpaces();

        var result = 0;
        var tangentDirection = corridor.direction().getClockWise();
        var corridorX = corridor.position().getX() + roomPosition.getX();
        var sliceStartY = corridor.position().getY() + roomPosition.getY() - origin.getY() - 2;
        var corridorZ = corridor.position().getZ() + roomPosition.getZ();
        var z = 1 + offset;
        for (int x = -2; x <= 2; x++) {
            var positionX = corridorX + corridor.direction().getStepX() * z + tangentDirection.getStepX() * x;
            var positionZ = corridorZ + corridor.direction().getStepZ() * z + tangentDirection.getStepZ() * x;
            if (positionX < origin.getX() || positionX >= origin.getX() + size.getX() || positionZ < origin.getZ()
                    || positionZ >= origin.getZ() + size.getZ()) {
                continue;
            }
            positionX -= origin.getX();
            positionZ -= origin.getZ();
            var emptySpacesColumn = emptySpaces[positionX + positionZ * size.getX()];
            var shiftedColumn = sliceStartY < 0 ? (emptySpacesColumn << -sliceStartY)
                    : (emptySpacesColumn >>> sliceStartY);
            result |= (int) ((0x1f & shiftedColumn) << (x * 5 + 10));
        }
        return result;
    }

    @Override
    public void generateSection(
            LayeredRiftLayout.LayoutSection section,
            RandomSource source,
            ArrayList<RiftSpace> allSpaces) {
        // if (allSpaces.isEmpty()) {
        var room = roomRandomizer.randomSpace(source, new Vec3i(1, 1, 1));
        room = room.offset(
                section.sectionShape().getBoxStart().getX() + 1 + (section.sectionShape().getBoxSize().getX() >> 1), -2,
                section.sectionShape().getBoxStart().getZ() + 1 + (section.sectionShape().getBoxSize().getZ() >> 1));
        var room2 = roomRandomizer.randomSpace(source, new Vec3i(1, 1, 1));
        room2 = room2.offset(
                section.sectionShape().getBoxStart().getX() + 1 + (section.sectionShape().getBoxSize().getX() >> 1), 2,
                section.sectionShape().getBoxStart().getZ() + 1 + (section.sectionShape().getBoxSize().getZ() >> 1));
        allSpaces.add(room);
        allSpaces.add(room2);
        // }

        var currentSpaces = Collections.<RiftSpace>emptyList();
        var nextSpaces = new ArrayList(allSpaces);
        while (!nextSpaces.isEmpty()) {
            currentSpaces = nextSpaces;
            nextSpaces = new ArrayList<>();
            for (var space : currentSpaces) {
                for (var corridor : space.corridors()) {
                    var nextSpace = nextChaoticSpace(corridor, source, space.origin(), section);
                    if (nextSpace != null && section.tryPlaceSpace(nextSpace)) {
                        nextSpaces.add(nextSpace);
                        allSpaces.add(nextSpace);
                    }
                }
            }
        }
    }

    public static record Factory(RoomRandomizerImpl.Factory roomRandomizerFactory)
            implements LayeredRiftLayout.LayoutLayer.Factory {
        public static final MapCodec<Factory> CODEC = RecordCodecBuilder.mapCodec(it -> it.group(
                RoomRandomizerImpl.Factory.CODEC.fieldOf("room_randomizer").forGetter(Factory::roomRandomizerFactory)
        ).apply(it, Factory::new));

        @Override
        public LayeredRiftLayout.LayoutLayer createLayer(MinecraftServer server, RiftConfig riftConfig) {
            return new ChaosLayer(roomRandomizerFactory.createRandomizer(server));
        }

        @Override
        public MapCodec<? extends LayeredRiftLayout.LayoutLayer.Factory> codec() {
            return CODEC;
        }
    }

    static {
        var protoMasks = new int[256];
        for (int size = 0; size < 16; size++) {
            var width = size & 3;
            var height = size >> 2;
            var baseMask = 1;
            for (int i = 0; i < height; i++) {
                baseMask |= (baseMask << 1);
            }
            for (int i = 0; i < width; i++) {
                baseMask |= (baseMask << 5);
            }

            for (int position = 0; position < 16; position++) {
                var x = position & 0b11;
                var y = position >> 2;
                var index = position
                        | (((width & 1) | ((height & 1) << 1) | ((width & 2) << 1) | ((height & 2) << 2)) << 4);
                protoMasks[index] = baseMask << (y + 5 * x);
                if ((protoMasks[index] & 0b00000_00000_00100_00000_00000) == 0 || x == 3 || y == 3 || width == 3
                        || height == 3) {
                    protoMasks[index] = -1;
                }
            }
        }
        MASKS = IntImmutableList.of(protoMasks);
    }
}
