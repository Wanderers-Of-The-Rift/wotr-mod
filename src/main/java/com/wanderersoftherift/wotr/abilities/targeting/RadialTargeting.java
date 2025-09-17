package com.wanderersoftherift.wotr.abilities.targeting;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.effects.predicate.TargetBlockPredicate;
import com.wanderersoftherift.wotr.abilities.effects.predicate.TargetEntityPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class RadialTargeting extends AreaTargeting {
    public static final MapCodec<RadialTargeting> CODEC = RecordCodecBuilder.mapCodec(
            instance -> commonFields(instance).apply(instance, RadialTargeting::new));

    public RadialTargeting(TargetEntityPredicate entities, TargetBlockPredicate blocks, float range,
            boolean alignToBlock) {
        super(entities, blocks, range, alignToBlock);
    }

    @Override
    public MapCodec<? extends AbilityTargeting> getCodec() {
        return CODEC;
    }

    @Override
    protected AABB getArea(HitResult source, float range) {
        if (getAlignToBlock()) {
            return AABB.ofSize(BlockPos.containing(source.getLocation()).getCenter(), 2 * range, 2 * range, 2 * range);
        }
        return AABB.ofSize(source.getLocation(), 2 * range, 2 * range, 2 * range);
    }

    @Override
    protected boolean inArea(Vec3 center, Vec3 pos, float range) {
        return pos.distanceToSqr(center) < range * range;
    }

    @Override
    protected boolean inArea(Vec3 center, BlockPos pos, float range) {
        return pos.closerToCenterThan(center, range);
    }
}
