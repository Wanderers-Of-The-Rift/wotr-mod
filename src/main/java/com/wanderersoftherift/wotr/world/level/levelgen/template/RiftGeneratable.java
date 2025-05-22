package com.wanderersoftherift.wotr.world.level.levelgen.template;

import com.wanderersoftherift.wotr.util.JavaRandomFromRandomSource;
import com.wanderersoftherift.wotr.util.TripleMirror;
import com.wanderersoftherift.wotr.world.level.levelgen.RiftProcessedRoom;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.entity.JigsawBlockEntity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public interface RiftGeneratable {

    void processAndPlace(
            RiftProcessedRoom destination,
            ServerLevelAccessor world,
            Vec3i placementShift,
            TripleMirror mirror);

    Collection<StructureTemplate.JigsawBlockInfo> jigsaws();

    Vec3i size();

    String identifier();

    static void generate(
            RiftGeneratable generatable,
            RiftProcessedRoom destination,
            ServerLevelAccessor world,
            Vec3i placementShift,
            TripleMirror mirror,
            MinecraftServer server,
            RandomSource random,
            long[] mask) {
        if (collidesWithMask(generatable, mask, placementShift, mirror)) {
            return;
        }
        destination.clearNewFlags();
        if (mask == null) {
            mask = new long[16 * 16 * 16];
        }
        var jigsawList = new ArrayList<>(generatable.jigsaws());
        Collections.shuffle(jigsawList, JavaRandomFromRandomSource.of(random));

        for (var jigsaw : jigsawList) {
            var pool = jigsaw.pool();
            if (isPoolBlacklisted(pool)) {
                continue;
            }
            var next = RiftTemplates.random(server, pool, random);
            if (next == null) {
                continue;
            }

            var simplifiedDirection1 = simplifiedDirection(jigsaw, mirror);
            var simplifiedDirection1Opposite = simplifiedDirection1.getOpposite();

            var jigsaw2List = next.jigsaws()
                    .stream()
                    .filter(
                            (otherJigsaw) -> {
                                var otherSimplifiedDirection = simplifiedDirection(otherJigsaw, TripleMirror.NONE);
                                return otherJigsaw.name().equals(jigsaw.target())
                                        && (simplifiedDirection1Opposite == otherSimplifiedDirection
                                                || (simplifiedDirection1.getStepY() == 0 && otherSimplifiedDirection
                                                        .getStepY() == 0 /* checks if both directions are horizontal */));
                            })
                    .toList();

            if (jigsaw2List.isEmpty()) {
                continue;// todo possibly multiple attempts
            }
            var jigsaw2 = jigsaw2List.get(random.nextInt(jigsaw2List.size()));
            var simplifiedDirection2 = simplifiedDirection(jigsaw2, TripleMirror.NONE);
            var nextMirrorInt = random.nextInt(8);
            if (simplifiedDirection1.getAxis() == Direction.Axis.Y) {
                if (simplifiedDirection2 != simplifiedDirection1Opposite) {
                    continue;
                }
                if (jigsaw.jointType() == JigsawBlockEntity.JointType.ALIGNED
                        && jigsaw2.jointType() == JigsawBlockEntity.JointType.ALIGNED) {
                    nextMirrorInt = mirrorCorrection(auxiliaryDirection(jigsaw, mirror),
                            auxiliaryDirection(jigsaw2, TripleMirror.NONE), nextMirrorInt, false);
                }
            } else {
                nextMirrorInt = mirrorCorrection(simplifiedDirection1, simplifiedDirection2, nextMirrorInt, true);
            }
            var nextMirror = new TripleMirror(nextMirrorInt);
            var newPlacementShift = placementShift
                    .offset(mirror.applyToPosition(jigsaw.info().pos(), generatable.size().getX() - 1,
                            generatable.size().getZ() - 1))
                    .offset(nextMirror
                            .applyToPosition(jigsaw2.info().pos(), next.size().getX() - 1, next.size().getZ() - 1)
                            .multiply(-1));

            generate(next, destination, world, newPlacementShift.relative(simplifiedDirection1), nextMirror, server,
                    random, mask);
        }
        writeCollisionMask(generatable, mask, placementShift, mirror);
        generatable.processAndPlace(destination, world, placementShift, mirror);
    }

    static boolean collidesWithMask(
            RiftGeneratable riftGeneratable,
            long[] mask,
            Vec3i placementShift,
            TripleMirror mirror) {
        if (mask == null) {
            return false;
        }
        var size = new TripleMirror(false, false, mirror.diagonal()).applyToPosition(riftGeneratable.size(), 0, 0);
        for (int y = 0; y < size.getY(); y += 4) {
            for (int z = 0; z < size.getZ(); z += 4) {
                for (int x = 0; x < size.getX(); x += 4) {
                    var lx = (x + placementShift.getX()) >> 2;
                    var ly = (y + placementShift.getY()) >> 2;
                    var lz = (z + placementShift.getZ()) >> 2;
                    if (lx < 0 || ly < 0 || lz < 0) {
                        continue;
                    }
                    var maskedValue = mask[lx | (ly << 8) | (lz << 4)];
                    if (maskedValue == 0) {
                        continue;
                    }
                    var newMask = -1L;
                    if (placementShift.getX() > (lx << 2) || placementShift.getX() + size.getX() < (lx << 2) + 4) {
                        var newMaskX = 0L;
                        for (int passX = 0; passX < 4; passX++) {
                            var actualX = passX + (lx << 2);
                            if (actualX >= placementShift.getX()) {
                                if (actualX >= placementShift.getX() + size.getX()) {
                                    break;
                                }
                                newMaskX |= 0b0001_0001_0001_0001_0001_0001_0001_0001_0001_0001_0001_0001_0001_0001_0001_0001L << passX;
                            }
                        }
                        newMask &= newMaskX;
                        if ((maskedValue & newMask) == 0) {
                            continue;
                        }
                    }
                    if (placementShift.getY() > (ly << 2) || placementShift.getY() + size.getY() < (ly << 2) + 4) {
                        var newMaskY = 0L;
                        for (int passY = 0; passY < 4; passY++) {
                            var actualY = passY + (ly << 2);
                            if (actualY >= placementShift.getY()) {
                                if (actualY >= placementShift.getY() + size.getY()) {
                                    break;
                                }
                                newMaskY |= 0b1111_1111_1111_1111L << (passY << 4);
                            }
                        }
                        newMask &= newMaskY;
                        if ((maskedValue & newMask) == 0) {
                            continue;
                        }
                    }
                    if (placementShift.getZ() > (lz << 2) || placementShift.getZ() + size.getZ() < (lz << 2) + 4) {
                        var newMaskZ = 0L;
                        for (int passZ = 0; passZ < 4; passZ++) {
                            var actualZ = passZ + (lz << 2);
                            if (actualZ >= placementShift.getZ()) {
                                if (actualZ >= placementShift.getZ() + size.getZ()) {
                                    break;
                                }
                                newMaskZ |= 0b0000_0000_0000_1111_0000_0000_0000_1111_0000_0000_0000_1111_0000_0000_0000_1111L << (passZ << 2);
                            }
                        }
                        newMask &= newMaskZ;
                        if ((maskedValue & newMask) == 0) {
                            continue;
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }

    static void writeCollisionMask(
            RiftGeneratable riftGeneratable,
            long[] mask,
            Vec3i placementShift,
            TripleMirror mirror) {
        if (mask == null) {
            return;
        }
        var size = new TripleMirror(false, false, mirror.diagonal()).applyToPosition(riftGeneratable.size(), 0, 0);
        for (int y = 0; y < size.getY(); y += 4) {
            for (int z = 0; z < size.getZ(); z += 4) {
                for (int x = 0; x < size.getX(); x += 4) {
                    var lx = (x + placementShift.getX()) >> 2;
                    var ly = (y + placementShift.getY()) >> 2;
                    var lz = (z + placementShift.getZ()) >> 2;
                    if (lx < 0 || ly < 0 || lz < 0) {
                        continue;
                    }
                    var newMask = -1L;
                    var newMaskX = 0L;
                    if (placementShift.getX() > (lx << 2) || placementShift.getX() + size.getX() < (lx << 2) + 4) {
                        for (int passX = 0; passX < 4; passX++) {
                            var actualX = passX + (lx << 2);
                            if (actualX >= placementShift.getX()) {
                                if (actualX >= placementShift.getX() + size.getX()) {
                                    break;
                                }
                                newMaskX |= 0b0001_0001_0001_0001_0001_0001_0001_0001_0001_0001_0001_0001_0001_0001_0001_0001L << passX;
                            }
                        }
                        newMask &= newMaskX;
                    }
                    var newMaskY = 0L;
                    if (placementShift.getY() > (ly << 2) || placementShift.getY() + size.getY() < (ly << 2) + 4) {
                        for (int passY = 0; passY < 4; passY++) {
                            var actualY = passY + (ly << 2);
                            if (actualY >= placementShift.getY()) {
                                if (actualY >= placementShift.getY() + size.getY()) {
                                    break;
                                }
                                newMaskY |= 0b1111_1111_1111_1111L << (passY << 4);
                            }
                        }
                        newMask &= newMaskY;
                    }
                    var newMaskZ = 0L;
                    if (placementShift.getZ() > (lz << 2) || placementShift.getZ() + size.getZ() < (lz << 2) + 4) {
                        for (int passZ = 0; passZ < 4; passZ++) {
                            var actualZ = passZ + (lz << 2);
                            if (actualZ >= placementShift.getZ()) {
                                if (actualZ >= placementShift.getZ() + size.getZ()) {
                                    break;
                                }
                                newMaskZ |= 0b0000_0000_0000_1111_0000_0000_0000_1111_0000_0000_0000_1111_0000_0000_0000_1111L << (passZ << 2);
                            }
                        }
                        newMask &= newMaskZ;
                    }
                    mask[lx | (ly << 8) | (lz << 4)] |= newMask;
                }
            }
        }
    }

    private static int setOrClearBit(int value, int bit, boolean set) {
        value &= ~bit;
        if (set) {
            value |= bit;
        }
        return value;
    }

    private static int mirrorCorrection(Direction d1, Direction d2, int oldMirrorInt, boolean targetOpposite) {
        return setOrClearBit(
                setOrClearBit(oldMirrorInt, 0b100, d1.getAxis() != d2.getAxis()),
                d2.getAxis() == Direction.Axis.Z ? 0b010 : 0b001,
                ((d1.getStepX() + d1.getStepZ()) > 0 == (d2.getStepX() + d2.getStepZ()) > 0) == targetOpposite
        );
    }

    private static Direction simplifiedDirection(StructureTemplate.JigsawBlockInfo jigsaw, TripleMirror mirror) {
        return mirror.applyToBlockState(jigsaw.info().state()).getValue(JigsawBlock.ORIENTATION).front();
    }

    private static Direction auxiliaryDirection(StructureTemplate.JigsawBlockInfo jigsaw, TripleMirror mirror) {
        return mirror.applyToBlockState(jigsaw.info().state()).getValue(JigsawBlock.ORIENTATION).top();
    }

    private static boolean isPoolBlacklisted(ResourceLocation pool) {
        return pool.getPath().contains("rift/ring_") && "wotr".equals(pool.getNamespace());
    }

}
