package com.wanderersoftherift.wotr.util;

import net.minecraft.util.RandomSource;

import java.util.random.RandomGenerator;

public record JavaRandomFromRandomSource(RandomSource randomSource) implements RandomGenerator {
    @Override
    public long nextLong() {
        return randomSource().nextLong();
    }

    public static RandomGenerator of(RandomSource source) {
        if (source instanceof RandomSourceFromJavaRandom random) {
            return random.getJavaRandom();
        }
        return new JavaRandomFromRandomSource(source);
    }
}
