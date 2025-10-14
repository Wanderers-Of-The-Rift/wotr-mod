package com.wanderersoftherift.wotr.abilities.targeting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import com.wanderersoftherift.wotr.abilities.effects.predicate.TargetBlockPredicate;
import com.wanderersoftherift.wotr.abilities.effects.predicate.TargetEntityPredicate;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * This targeting selects a single target via raycast
 */
public record FieldOfViewTargeting(TargetEntityPredicate entityPredicate, TargetBlockPredicate blockPredicate,
        double range, double cosine) implements AbilityTargeting {

    public static final MapCodec<FieldOfViewTargeting> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    TargetEntityPredicate.CODEC.optionalFieldOf("entities", TargetEntityPredicate.Trivial.ALL)
                            .forGetter(FieldOfViewTargeting::entityPredicate),
                    TargetBlockPredicate.CODEC.optionalFieldOf("blocks", TargetBlockPredicate.Trivial.ALL)
                            .forGetter(FieldOfViewTargeting::blockPredicate),
                    Codec.DOUBLE.fieldOf("range").forGetter(FieldOfViewTargeting::range),
                    Codec.DOUBLE.fieldOf("angle_cosine").forGetter(FieldOfViewTargeting::cosine)
            ).apply(instance, FieldOfViewTargeting::new));

    @Override
    public MapCodec<? extends AbilityTargeting> getCodec() {
        return CODEC;
    }

    @Override
    public List<TargetInfo> getTargets(AbilityContext context, TargetInfo origin) {
        return origin.targets().stream().map(source -> {
            if (source instanceof EntityHitResult entitySource) {
                var entity = entitySource.getEntity();
                var start = entity.getEyePosition();
                var dirLength = entitySource.getEntity()
                        .calculateViewVector(entity.getXRot(), entity.getYRot())
                        .scale(range);
                return new TargetInfo(source,
                        (List<HitResult>) getEntityTargets(context, start, dirLength, source).toList());
            } else if (source instanceof BlockHitResult blockSource) {
                var start = source.getLocation();
                var dirLength = blockSource.getDirection().getUnitVec3().scale(range);
                return new TargetInfo(source,
                        (List<HitResult>) getEntityTargets(context, start, dirLength, source).toList());
            }
            return null;
        }).filter(Objects::nonNull).toList();
    }

    private Stream<? extends HitResult> getEntityTargets(
            AbilityContext context,
            Vec3 start,
            Vec3 dirLength,
            HitResult source) {
        var area = new AABB(start.x - range, start.y - range, start.z - range, start.x + range, start.y + range,
                start.z + range);
        var allEntities = TargetingUtil.getEntitiesInArea(context.level(), area,
                entity -> entityPredicate.matches(entity, source, context));
        return allEntities.stream().map(entityHitResult -> {
            var box = entityHitResult.getEntity().getBoundingBox();
            var boxPoint = findClosestToRay(box, new Ray(start, dirLength));
            return new EntityHitResult(entityHitResult.getEntity(), boxPoint);
        }).filter(entityHitResult -> {
            var entityHitDirection = entityHitResult.getLocation().subtract(start).normalize();
            var angle = entityHitDirection.dot(dirLength.normalize());
            return angle > this.cosine;
        });
    }

    private Vec3 findClosestToRay(AABB box, Ray ray) {
        var boxPoint = box.getCenter();
        var rayPoint = closestTo(ray, boxPoint);
        for (int i = 0; i < 5; i++) {
            var boxPointOld = boxPoint;
            var rayPointOld = rayPoint;
            boxPoint = closestTo(box, rayPoint);
            rayPoint = closestTo(ray, boxPoint);
            if (boxPoint.equals(boxPointOld) || rayPoint.equals(rayPointOld)) {
                break;
            }
        }
        return boxPoint;

    }

    private static Vec3 closestTo(AABB boundingBox, Vec3 otherPos) {
        return new Vec3(Math.max(boundingBox.minX, Math.min(otherPos.x, boundingBox.maxX)),
                Math.max(boundingBox.minY, Math.min(otherPos.y, boundingBox.maxY)),
                Math.max(boundingBox.minZ, Math.min(otherPos.z, boundingBox.maxZ)));
    }

    private static Vec3 closestTo(Ray ray, Vec3 otherPos) {
        var rayLength = ray.dirLength.length();
        var rayDirectionNormalized = ray.dirLength.normalize();

        var posOffset = otherPos.subtract(ray.start);

        var dot = posOffset.dot(rayDirectionNormalized);
        if (dot < 0) {
            return ray.start;
        }
        if (dot > rayLength) {
            return ray.start.add(ray.dirLength);
        }
        return ray.start.add(rayDirectionNormalized.multiply(new Vec3(dot, dot, dot)));
    }

    record Ray(Vec3 start, Vec3 dirLength) {
    }
}
