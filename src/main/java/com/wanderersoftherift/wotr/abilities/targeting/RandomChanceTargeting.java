package com.wanderersoftherift.wotr.abilities.targeting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import net.minecraft.util.RandomSource;

import java.util.List;

public record RandomChanceTargeting(float chance) implements AbilityTargeting {
    public static final MapCodec<RandomChanceTargeting> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.floatRange(0, 1).fieldOf("chance").forGetter(RandomChanceTargeting::chance)
    ).apply(instance, RandomChanceTargeting::new));

    @Override
    public MapCodec<? extends AbilityTargeting> getCodec() {
        return CODEC;
    }

    @Override
    public List<TargetInfo> getTargets(AbilityContext context, TargetInfo origin) {
        if (chance <= 0) {
            return List.of();
        }
        if (chance >= 1) {
            return List.of(origin);
        }
        RandomSource random = context.level().getRandom();
        return List.of(new TargetInfo(origin.source(),
                origin.targets().stream().filter(x -> random.nextFloat() <= chance).toList()));
    }
}
