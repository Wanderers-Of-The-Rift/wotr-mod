package com.wanderersoftherift.wotr.abilities.targeting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import com.wanderersoftherift.wotr.abilities.effects.predicate.TargetBlockPredicate;
import com.wanderersoftherift.wotr.abilities.effects.predicate.TargetEntityPredicate;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

/**
 * This targeting selects a single target via raycast
 */
public class RaycastTargeting implements AbilityTargeting {

    public static final MapCodec<RaycastTargeting> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    TargetEntityPredicate.CODEC.optionalFieldOf("entities", TargetEntityPredicate.ALL)
                            .forGetter(RaycastTargeting::getEntityPredicate),
                    TargetBlockPredicate.CODEC.optionalFieldOf("blocks", TargetBlockPredicate.ALL)
                            .forGetter(RaycastTargeting::getBlockPredicate),
                    Codec.DOUBLE.fieldOf("range").forGetter(RaycastTargeting::getRange)
            ).apply(instance, RaycastTargeting::new));

    private final TargetEntityPredicate entityPredicate;
    private final TargetBlockPredicate blockPredicate;
    private final double range;

    public RaycastTargeting(TargetEntityPredicate entityPredicate, TargetBlockPredicate blockPredicate, double range) {
        this.entityPredicate = entityPredicate;
        this.blockPredicate = blockPredicate;
        this.range = range;
    }

    public double getRange() {
        return range;
    }

    public TargetEntityPredicate getEntityPredicate() {
        return entityPredicate;
    }

    public TargetBlockPredicate getBlockPredicate() {
        return blockPredicate;
    }

    @Override
    public MapCodec<? extends AbilityTargeting> getCodec() {
        return CODEC;
    }

    @Override
    public List<TargetInfo> getTargets(AbilityContext context, TargetInfo origin) {
        List<TargetInfo> result = new ArrayList<>();
        for (HitResult source : origin.targets()) {
            Entity entity = null;
            Vec3 start;
            Vec3 end;
            if (source instanceof EntityHitResult entitySource) {
                entity = entitySource.getEntity();
                start = entity.getEyePosition();
                end = start.add(
                        entitySource.getEntity().calculateViewVector(entity.getXRot(), entity.getYRot()).scale(range));
            } else if (source instanceof BlockHitResult blockSource) {
                start = source.getLocation();
                end = start.add(blockSource.getDirection().getUnitVec3().scale(range));
            } else {
                continue;
            }
            HitResult hit = TargetingUtil.rayTrace(start, end, 0, 0, context.level(),
                    e -> entityPredicate.matches(e, source, context), entity);
            if (hit instanceof BlockHitResult blockHitResult
                    && !blockPredicate.matches(blockHitResult.getBlockPos(), source, context)) {
                continue;
            }
            if (hit.getType() != HitResult.Type.MISS) {
                result.add(new TargetInfo(source, List.of(hit)));
            }
        }

        return result;
    }
}
