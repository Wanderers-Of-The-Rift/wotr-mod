package com.wanderersoftherift.wotr.abilities.targeting;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Utility methods to assist with targeting
 */
public final class TargetingUtil {

    private TargetingUtil() {
    }

    /**
     * @param level           The level to find entities in
     * @param area            The AABB area to find entities in
     * @param entityPredicate Predicate for what entities to accept
     * @return A list of entities within the area that meet all conditions
     */
    public static List<EntityHitResult> getEntitiesInArea(Level level, AABB area, Predicate<Entity> entityPredicate) {
        return level.getEntities((Entity) null, area, entityPredicate)
                .stream()
                .map(entity -> new EntityHitResult(entity, entity.position()))
                .toList();
    }

    /**
     * @param area           The AABB area to find blocks within
     * @param blockPredicate Predicate for determining whether a block should be accepted
     * @return A list of blocks within the area that meet all conditions
     */
    public static List<BlockHitResult> getBlocksInArea(AABB area, Predicate<BlockPos> blockPredicate) {
        Vec3 center = area.getCenter();
        List<BlockHitResult> results = new ArrayList<>();
        for (BlockPos pos : BlockPos.betweenClosed(area)) {
            if (blockPredicate.test(pos)) {
                // TODO: how should we set isInside?
                results.add(new BlockHitResult(pos.getCenter(),
                        Direction.getApproximateNearest(pos.getCenter().subtract(center)), new BlockPos(pos), false));
            }
        }
        return results;
    }

    /**
     * Ray trace hitting both blocks and entities, stopping on first hit
     * 
     * @param start  Start of the ray
     * @param end    End of the ray
     * @param size   Size of the ray
     * @param margin Margin for hitting
     * @param level  Level to rayTrace within
     * @param filter Predicate for what entities to hit
     * @param origin Entity that is the making the trace (its hit box will be used)
     * @return A hit result for the first entity or block hit
     */
    public static HitResult rayTrace(
            Vec3 start,
            Vec3 end,
            float size,
            float margin,
            Level level,
            Predicate<Entity> filter,
            @Nullable Entity origin) {
        HitResult hitResult = level.clipIncludingBorder(new ClipContext(start, end, ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE, CollisionContext.empty()));
        if (hitResult.getType() != HitResult.Type.MISS) {
            end = hitResult.getLocation();
        }

        HitResult entityHitResult = ProjectileUtil.getEntityHitResult(
                level, origin, start, end, new AABB(start, end).expandTowards(end.subtract(start)).inflate(size),
                filter, margin
        );
        if (entityHitResult != null) {
            hitResult = entityHitResult;
        }

        return hitResult;
    }

}
