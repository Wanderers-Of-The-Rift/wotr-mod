package com.wanderersoftherift.wotr.util;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;

public class VoxelShapeUtils {
    protected static final Direction[] HORIZONTAL_DIRECTIONS = { Direction.NORTH, Direction.SOUTH, Direction.WEST,
            Direction.EAST };
    private static final Direction[] DIRECTIONS = Direction.values();

    /**
     * Rotates an {@link AABB} to a given direction
     *
     * @param box  The {@link AABB} to rotate
     * @param side The side to rotate it to.
     *
     * @return The rotated {@link AABB}
     */
    public static AABB rotate(AABB box, Direction side) {
        return switch (side) {
            case DOWN -> box;
            case UP -> new AABB(box.minX, -box.minY, -box.minZ, box.maxX, -box.maxY, -box.maxZ);
            case NORTH -> new AABB(box.minX, -box.minZ, box.minY, box.maxX, -box.maxZ, box.maxY);
            case SOUTH -> new AABB(-box.minX, -box.minZ, -box.minY, -box.maxX, -box.maxZ, -box.maxY);
            case WEST -> new AABB(box.minY, -box.minZ, -box.minX, box.maxY, -box.maxZ, -box.maxX);
            case EAST -> new AABB(-box.minY, -box.minZ, box.minX, -box.maxY, -box.maxZ, box.maxX);
        };
    }

    /**
     * Rotates an {@link AABB} according to a specific rotation.
     *
     * @param box      The {@link AABB} to rotate
     * @param rotation The rotation we are performing.
     *
     * @return The rotated {@link AABB}
     */
    public static AABB rotate(AABB box, Rotation rotation) {
        return switch (rotation) {
            case NONE -> box;
            case CLOCKWISE_90 -> new AABB(-box.minZ, box.minY, box.minX, -box.maxZ, box.maxY, box.maxX);
            case CLOCKWISE_180 -> new AABB(-box.minX, box.minY, -box.minZ, -box.maxX, box.maxY, -box.maxZ);
            case COUNTERCLOCKWISE_90 -> new AABB(box.minZ, box.minY, -box.minX, box.maxZ, box.maxY, -box.maxX);
        };
    }

    /**
     * Rotates a {@link VoxelShape} by applying a transformation function to each {@link AABB} it contains.
     *
     * @param shape          The {@link VoxelShape} to be rotated.
     * @param data           Additional data to be passed to the transformation function.
     * @param rotateFunction A function that defines how each {@link AABB} should be transformed.
     *
     * @return A new {@link VoxelShape} with all bounding boxes rotated accordingly.
     */
    public static <T> VoxelShape rotate(VoxelShape shape, T data, BiFunction<AABB, T, AABB> rotateFunction) {
        Vec3 fromOrigin = new Vec3(-0.5, -0.5, -0.5);

        List<VoxelShape> rotatedPieces = new ArrayList<>();
        List<AABB> boundingBoxes = shape.toAabbs(); // Extract individual bounding boxes

        for (AABB boundingBox : boundingBoxes) {
            // Shift the bounding box to be centered at the origin, apply the rotation, then move it back
            rotatedPieces.add(
                    Shapes.create(rotateFunction.apply(boundingBox.move(fromOrigin.x, fromOrigin.y, fromOrigin.z), data)
                            .move(-fromOrigin.x, -fromOrigin.y, -fromOrigin.z)));
        }

        return combine(rotatedPieces); // Combine rotated bounding boxes into a single VoxelShape
    }

    /**
     * Rotates an {@link AABB} to a specific horizontal direction.
     *
     * @param box  The {@link AABB} to rotate
     * @param side The direction to rotate it to.
     *
     * @return The rotated {@link AABB}
     */
    public static AABB rotateHorizontal(AABB box, Direction side) {
        return switch (side) {
            case NORTH -> rotate(box, Rotation.NONE);
            case SOUTH -> rotate(box, Rotation.CLOCKWISE_180);
            case WEST -> rotate(box, Rotation.COUNTERCLOCKWISE_90);
            case EAST -> rotate(box, Rotation.CLOCKWISE_90);
            default -> box;
        };
    }

    /**
     * Rotates a {@link VoxelShape} to a specific side
     *
     * @param shape The {@link VoxelShape} to rotate
     * @param side  The side to rotate it to.
     *
     * @return The rotated {@link VoxelShape}
     */
    public static VoxelShape rotate(VoxelShape shape, Direction side) {
        return rotate(shape, side, VoxelShapeUtils::rotate);
    }

    /**
     * Rotates a {@link VoxelShape} to a specific side horizontally.
     *
     * @param shape The {@link VoxelShape} to rotate
     * @param side  The side to rotate it to.
     *
     * @return The rotated {@link VoxelShape}
     */
    public static VoxelShape rotateHorizontal(VoxelShape shape, Direction side) {
        return rotate(shape, side, VoxelShapeUtils::rotateHorizontal);
    }

    /**
     * Used to combine a collection of VoxelShapes
     *
     * @param shapes The collection of {@link VoxelShape}s to combine
     *
     * @return A simplified {@link VoxelShape} including everything that is part of the input shapes.
     */
    public static VoxelShape combine(Collection<VoxelShape> shapes) {
        return batchCombine(Shapes.empty(), BooleanOp.OR, true, shapes);
    }

    /**
     * Used for mass combining shapes
     *
     * @param shapes The list of {@link VoxelShape}s to include
     *
     * @return A simplified {@link VoxelShape} including everything that is part of the input shapes.
     */
    public static VoxelShape combine(VoxelShape... shapes) {
        return batchCombine(Shapes.empty(), BooleanOp.OR, true, shapes);
    }

    /**
     * Used for mass combining shapes using a specific {@link BooleanOp} and a given start shape.
     *
     * @param base     The {@link VoxelShape} to start with
     * @param function The {@link BooleanOp} to perform
     * @param simplify If true, the shape will be optimized via {@link VoxelShape#optimize()},
     * @param shapes   The collection of {@link VoxelShape}s to include
     *
     * @return A combined {@link VoxelShape} based on the input parameters.
     */
    public static VoxelShape batchCombine(VoxelShape base, BooleanOp function, boolean simplify,
            Collection<VoxelShape> shapes) {
        VoxelShape combinedShape = base;
        for (VoxelShape shape : shapes) {
            combinedShape = Shapes.joinUnoptimized(combinedShape, shape, function);
        }
        if (simplify) {
            return combinedShape.optimize();
        } else {
            return combinedShape;
        }
    }

    /**
     * Used for mass combining shapes using a specific {@link BooleanOp} and a given start shape.
     *
     * @param base     The {@link VoxelShape} to start with
     * @param function The {@link BooleanOp} to perform
     * @param simplify If true, the shape will be optimized via {@link VoxelShape#optimize()},
     * @param shapes   The list of {@link VoxelShape}s to include
     *
     * @return A {@link VoxelShape} based on the input parameters.
     */
    public static VoxelShape batchCombine(VoxelShape base, BooleanOp function, boolean simplify, VoxelShape... shapes) {
        VoxelShape combinedShape = base;
        for (VoxelShape shape : shapes) {
            combinedShape = Shapes.joinUnoptimized(combinedShape, shape, function);
        }
        if (simplify) {
            return combinedShape.optimize();
        } else {
            return combinedShape;
        }
    }

    /**
     * Populates an array with rotated versions of a given {@link VoxelShape}, adjusting for either vertical or
     * horizontal orientation.
     *
     * @param shape        The base {@link VoxelShape} to rotate.
     * @param dest         The destination array to store the rotated shapes.
     * @param verticalAxis If true, rotates the shape along all directions, otherwise, rotates it horizontally.
     * @param invert       If true, reverses the rotation direction by using the opposite side.
     */
    public static void setShape(VoxelShape shape, VoxelShape[] dest, boolean verticalAxis, boolean invert) {
        Direction[] dirs;
        if (verticalAxis) {
            dirs = DIRECTIONS;
        } else {
            dirs = HORIZONTAL_DIRECTIONS;
        }
        for (Direction side : dirs) {
            dest[verticalAxis ? side.ordinal() : side.ordinal() - 2] = verticalAxis
                    ? VoxelShapeUtils.rotate(shape, invert ? side.getOpposite() : side)
                    : VoxelShapeUtils.rotateHorizontal(shape, side);
        }
    }

    /**
     * Populates an array with horizontally rotated versions of a given {@link VoxelShape}.
     *
     * @param shape The base {@link VoxelShape} to rotate.
     * @param dest  The destination array to store the rotated shapes.
     */
    public static void setShape(VoxelShape shape, VoxelShape[] dest) {
        setShape(shape, dest, false, false);
    }
}
