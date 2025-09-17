package com.wanderersoftherift.wotr.abilities.targeting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import com.wanderersoftherift.wotr.util.RandomUtil;

import java.util.List;

public record RandomSubsetTargeting(int count) implements AbilityTargeting {

    public static final MapCodec<RandomSubsetTargeting> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.intRange(0, Integer.MAX_VALUE).fieldOf("count").forGetter(RandomSubsetTargeting::count)
    ).apply(instance, RandomSubsetTargeting::new));

    @Override
    public MapCodec<? extends AbilityTargeting> getCodec() {
        return CODEC;
    }

    @Override
    public List<TargetInfo> getTargets(AbilityContext context, TargetInfo origin) {
        if (origin.targets().size() <= count) {
            return List.of(origin);
        }
        return List.of(new TargetInfo(origin.source(),
                RandomUtil.randomSubset(origin.targets(), count, context.level().getRandom()).stream().toList()));
    }
}
