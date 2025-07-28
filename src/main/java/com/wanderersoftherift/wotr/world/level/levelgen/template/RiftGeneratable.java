package com.wanderersoftherift.wotr.world.level.levelgen.template;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.util.TripleMirror;
import com.wanderersoftherift.wotr.world.level.levelgen.RiftProcessedRoom;
import com.wanderersoftherift.wotr.world.level.levelgen.jigsaw.JigsawListProcessor;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.entity.JigsawBlockEntity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public interface RiftGeneratable {

    Codec<RiftGeneratable> BUILTIN_GENERATABLE_CODEC = WotrRegistries.RIFT_BUILTIN_GENERATABLE_TYPES.byNameCodec()
            .dispatch(fac -> fac.codec(), codec -> codec);

    MapCodec<? extends RiftGeneratable> codec();

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
            long[] mask,
            List<JigsawListProcessor> jigsawProcessors) {
        if (collidesWithMask(generatable, mask, placementShift, mirror)) {
            return;
        }
        destination.clearNewFlags();
        if (mask == null) {
            mask = new long[16 * 16 * 16];
        }
        var jigsawList = new ArrayList<>(generatable.jigsaws());
        for (var jigsawHandler : jigsawProcessors) {
            jigsawHandler.processJigsaws(jigsawList, random);
        }

        for (var jigsaw : jigsawList) {
            var pool = jigsaw.pool();
            var next = RiftTemplates.random(server, pool, random);
            if (next == null) {
                if (!("minecraft".equals(pool.getNamespace()) && "empty".equals(pool.getPath()))) {
                    WanderersOfTheRift.LOGGER.info("empty pool {}", pool);
                }
                continue;
            }

            var parentPrimaryDirection = simplifiedDirection(jigsaw, mirror);
            var parentOppositeDirection = parentPrimaryDirection.getOpposite();

            var childJigsawList = next.jigsaws()
                    .stream()
                    .filter(
                            (otherJigsaw) -> {
                                var otherSimplifiedDirection = simplifiedDirection(otherJigsaw, TripleMirror.NONE);
                                var areDirectionsOpposing = parentOppositeDirection == otherSimplifiedDirection;
                                var areDirectionsHorizontal = parentPrimaryDirection.getStepY() == 0
                                        && otherSimplifiedDirection.getStepY() == 0;
                                return otherJigsaw.name().equals(jigsaw.target())
                                        && (areDirectionsOpposing || areDirectionsHorizontal);
                            })
                    .toList();

            if (childJigsawList.isEmpty()) {
                WanderersOfTheRift.LOGGER.info(
                        "failed to spawn poi {} in room {} at jigsaw location {}, report this to build team",
                        jigsaw.pool(), generatable.identifier(), jigsaw.info().pos());
                continue;
            }
            var childJigsaw = childJigsawList.get(random.nextInt(childJigsawList.size()));
            var childPrimaryDirection = simplifiedDirection(childJigsaw, TripleMirror.NONE);
            var nextMirrorInt = random.nextInt(8);
            if (parentPrimaryDirection.getAxis() == Direction.Axis.Y) {
                if (childPrimaryDirection != parentOppositeDirection) {
                    continue;
                }
                if (jigsaw.jointType() == JigsawBlockEntity.JointType.ALIGNED
                        && childJigsaw.jointType() == JigsawBlockEntity.JointType.ALIGNED) {
                    nextMirrorInt = mirrorCorrection(auxiliaryDirection(jigsaw, mirror),
                            auxiliaryDirection(childJigsaw, TripleMirror.NONE), nextMirrorInt, false);
                }
            } else {
                nextMirrorInt = mirrorCorrection(parentPrimaryDirection, childPrimaryDirection, nextMirrorInt, true);
            }
            var nextMirror = TripleMirror.PERMUTATIONS.get(nextMirrorInt);
            var newPlacementShift = placementShift
                    .offset(mirror.applyToPosition(jigsaw.info().pos(), generatable.size().getX() - 1,
                            generatable.size().getZ() - 1))
                    .offset(nextMirror
                            .applyToPosition(childJigsaw.info().pos(), next.size().getX() - 1, next.size().getZ() - 1)
                            .multiply(-1));

            generate(next, destination, world, newPlacementShift.relative(parentPrimaryDirection), nextMirror, server,
                    random, mask, jigsawProcessors);
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
        var size = mirror.onlyDiagonal().applyToPosition(riftGeneratable.size(), 0, 0);
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
                    newMask &= riftGeneratable.buildMaskAxis(placementShift.getX() - (lx << 2), size.getX(),
                            0b0001_0001_0001_0001_0001_0001_0001_0001_0001_0001_0001_0001_0001_0001_0001_0001L, 0);
                    if ((maskedValue & newMask) == 0) {
                        continue;
                    }
                    newMask &= riftGeneratable.buildMaskAxis(placementShift.getZ() - (lz << 2), size.getZ(),
                            0b0000_0000_0000_1111_0000_0000_0000_1111_0000_0000_0000_1111_0000_0000_0000_1111L, 2);
                    if ((maskedValue & newMask) == 0) {
                        continue;
                    }
                    newMask &= riftGeneratable.buildMaskAxis(placementShift.getY() - (ly << 2), size.getY(),
                            0b1111_1111_1111_1111L, 4);
                    if ((maskedValue & newMask) == 0) {
                        continue;
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
        var size = mirror.onlyDiagonal().applyToPosition(riftGeneratable.size(), 0, 0);
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
                    newMask &= riftGeneratable.buildMaskAxis(placementShift.getX() - (lx << 2), size.getX(),
                            0b0001_0001_0001_0001_0001_0001_0001_0001_0001_0001_0001_0001_0001_0001_0001_0001L, 0);
                    newMask &= riftGeneratable.buildMaskAxis(placementShift.getZ() - (lz << 2), size.getZ(),
                            0b0000_0000_0000_1111_0000_0000_0000_1111_0000_0000_0000_1111_0000_0000_0000_1111L, 2);
                    newMask &= riftGeneratable.buildMaskAxis(placementShift.getY() - (ly << 2), size.getY(),
                            0b1111_1111_1111_1111L, 4);
                    mask[lx | (ly << 8) | (lz << 4)] |= newMask;
                }
            }
        }
    }

    private long buildMaskAxis(int shiftSubtract, int size, long mask, int maskShiftShift) {
        var shiftSizeSubtract = shiftSubtract + size;
        if (shiftSubtract <= 0 && shiftSizeSubtract >= 4) {
            return -1;
        }
        var newMask = 0L;
        for (int passZ = 0; passZ < 4; passZ++) {
            var maskShift = passZ << maskShiftShift;
            if (passZ < shiftSubtract) {
                continue;
            }
            if (passZ >= shiftSizeSubtract) {
                break;
            }
            newMask |= mask << maskShift;
        }
        return newMask;
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

}
