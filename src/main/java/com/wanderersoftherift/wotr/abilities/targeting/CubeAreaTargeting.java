package com.wanderersoftherift.wotr.abilities.targeting;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.effects.predicate.TargetBlockPredicate;
import com.wanderersoftherift.wotr.abilities.effects.predicate.TargetEntityPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class CubeAreaTargeting extends AreaTargeting {
    public static final MapCodec<CubeAreaTargeting> CODEC = RecordCodecBuilder.mapCodec(
            instance -> commonFields(instance
            ).apply(instance, CubeAreaTargeting::new));

    public CubeAreaTargeting(TargetEntityPredicate entities, TargetBlockPredicate blocks, float range,
            boolean alignToBlock) {
        super(entities, blocks, range, alignToBlock);
    }

    @Override
    public MapCodec<? extends AbilityTargeting> getCodec() {
        return CODEC;
    }

    @Override
    protected AABB getArea(HitResult source, float range) {
        return AABB.ofSize(source.getLocation(), range * 2, range * 2, range * 2);
    }

    @Override
    protected boolean inArea(Vec3 center, Vec3 pos, float range) {
        return true;
    }

    @Override
    protected boolean inArea(Vec3 center, BlockPos pos, float range) {
        return true;
    }
}
