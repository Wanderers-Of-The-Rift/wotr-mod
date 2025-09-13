package com.wanderersoftherift.wotr.abilities.targeting;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.List;

public final class CasterTargeting implements AbilityTargeting {

    public static final CasterTargeting INSTANCE = new CasterTargeting();
    public static final MapCodec<CasterTargeting> CODEC = MapCodec.unit(INSTANCE);

    private CasterTargeting() {
    }

    @Override
    public MapCodec<? extends AbilityTargeting> getCodec() {
        return CODEC;
    }

    @Override
    public List<TargetInfo> getTargets(AbilityContext context, TargetInfo origin) {
        if (context.caster().isRemoved()) {
            return List.of();
        }
        List<HitResult> casterHit = List.of(new EntityHitResult(context.caster(), context.caster().position()));
        return origin.targets().stream().map(target -> new TargetInfo(target, casterHit)).toList();
    }
}
