package com.wanderersoftherift.wotr.abilities.targeting;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public final class TargetingUtil {

    private TargetingUtil() {
    }

    public static List<EntityHitResult> getEntitiesInArea(
            Level level,
            AABB area,
            BiPredicate<Vec3, Vec3> narrowPhaseCheck,
            Predicate<Entity> entityPredicate) {
        Vec3 center = area.getCenter();
        return level
                .getEntities((Entity) null, area,
                        (target) -> narrowPhaseCheck.test(center, target.position()) && entityPredicate.test(target))
                .stream()
                .map(entity -> new EntityHitResult(entity, entity.position()))
                .toList();
    }

    public static List<BlockHitResult> getBlocksInArea(
            AABB area,
            BiPredicate<Vec3, BlockPos> narrowPhaseCheck,
            Predicate<BlockPos> blockPredicate) {
        Vec3 center = area.getCenter();
        List<BlockHitResult> results = new ArrayList<>();
        for (BlockPos pos : BlockPos.betweenClosed(area)) {
            if (narrowPhaseCheck.test(center, pos) && blockPredicate.test(pos)) {
                // TODO: review isInside
                results.add(new BlockHitResult(pos.getCenter(),
                        Direction.getApproximateNearest(pos.getCenter().subtract(center)), new BlockPos(pos), false));
            }
        }
        return results;
    }

}
