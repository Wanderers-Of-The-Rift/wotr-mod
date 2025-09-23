package com.wanderersoftherift.wotr.abilities.targeting;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import com.wanderersoftherift.wotr.abilities.effects.predicate.TargetBlockPredicate;
import com.wanderersoftherift.wotr.abilities.effects.predicate.TargetEntityPredicate;
import com.wanderersoftherift.wotr.abilities.targeting.shape.TargetAreaShape;
import com.wanderersoftherift.wotr.modifier.effect.ModifierEffect;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

/**
 * Base type for targeting an area. Subtypes provide logic for the broad AABB to target, and the more narrow targeting
 * within that area (e.g. for a sphere the AABB is the box that contains it, and the targets in the corners are
 * rejected).
 */
public class AreaTargeting implements AbilityTargeting {
    public static final MapCodec<AreaTargeting> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            TargetAreaShape.DIRECT_CODEC.fieldOf("shape").forGetter(AreaTargeting::getShape),
            TargetEntityPredicate.CODEC.optionalFieldOf("entities", TargetEntityPredicate.Trivial.ALL)
                    .forGetter(AreaTargeting::getEntityPredicate),
            TargetBlockPredicate.CODEC.optionalFieldOf("blocks", TargetBlockPredicate.Trivial.NONE)
                    .forGetter(AreaTargeting::getBlockPredicate)
    ).apply(instance, AreaTargeting::new));

    private static final Vec3 FORWARDS = new Vec3(0, 0, -1);

    private final TargetEntityPredicate entityPredicate;
    private final TargetBlockPredicate blockPredicate;
    private final TargetAreaShape shape;

    public AreaTargeting(TargetAreaShape shape, TargetEntityPredicate entities, TargetBlockPredicate blocks) {
        this.shape = shape;
        this.entityPredicate = entities;
        this.blockPredicate = blocks;
    }

    @Override
    public MapCodec<? extends AbilityTargeting> getCodec() {
        return CODEC;
    }

    @Override
    public boolean isRelevant(ModifierEffect modifierEffect) {
        return shape.isRelevant(modifierEffect);
    }

    @Override
    public final List<TargetInfo> getTargets(AbilityContext context, TargetInfo origin) {
        if (!(context.level() instanceof ServerLevel level)) {
            return List.of();
        }
        List<TargetInfo> result = new ArrayList<>();
        for (HitResult source : origin.targets()) {
            Vec3 location = source.getLocation();
            Vec3 direction = getDirection(source);
            AABB aabb = shape.getAABB(location, direction, context);

            List<HitResult> hits = new ArrayList<>();
            if (entityPredicate != TargetEntityPredicate.Trivial.NONE) {
                var entityNarrowphase = shape.getEntityPredicate(location, direction, context);
                hits.addAll(TargetingUtil.getEntitiesInArea(level, aabb, (target) -> entityNarrowphase.test(target)
                        && entityPredicate.matches(target, source, context)));
            }
            if (blockPredicate != TargetBlockPredicate.Trivial.NONE) {
                var blockNarrowphase = shape.getBlockPredicate(location, direction, context);
                hits.addAll(TargetingUtil.getBlocksInArea(aabb,
                        (pos) -> blockNarrowphase.test(pos) && blockPredicate.matches(pos, source, context)));
            }
            if (!hits.isEmpty()) {
                result.add(new TargetInfo(source, hits));
            }
        }

        return result;
    }

    private Vec3 getDirection(HitResult source) {
        if (source instanceof EntityHitResult entityHitResult) {
            Entity entity = entityHitResult.getEntity();
            return entity.calculateViewVector(entity.getXRot(), entity.getYRot());
        } else if (source instanceof BlockHitResult blockHitResult) {
            return blockHitResult.getDirection().getUnitVec3();
        }
        return FORWARDS;
    }

    public TargetAreaShape getShape() {
        return shape;
    }

    public TargetEntityPredicate getEntityPredicate() {
        return entityPredicate;
    }

    public TargetBlockPredicate getBlockPredicate() {
        return blockPredicate;
    }
}
