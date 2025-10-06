package com.wanderersoftherift.wotr.abilities.targeting;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import net.minecraft.world.phys.HitResult;

import java.util.List;

/**
 * Source targeting selects the source as the target, from each target
 */
public final class SourceTargeting implements AbilityTargeting {

    public static final SourceTargeting INSTANCE = new SourceTargeting();
    public static final MapCodec<SourceTargeting> CODEC = MapCodec.unit(INSTANCE);

    private SourceTargeting() {
    }

    @Override
    public MapCodec<? extends AbilityTargeting> getCodec() {
        return CODEC;
    }

    @Override
    public List<TargetInfo> getTargets(AbilityContext context, TargetInfo origin) {
        List<HitResult> sourceHits = List.of(origin.source());
        return origin.targets().stream().map(target -> new TargetInfo(target, sourceHits)).toList();
    }
}
