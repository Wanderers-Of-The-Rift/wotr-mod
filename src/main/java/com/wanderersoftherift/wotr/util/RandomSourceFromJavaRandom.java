package com.wanderersoftherift.wotr.util;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;

import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;

public class RandomSourceFromJavaRandom implements RandomSource {

    private RandomGenerator javaRandom;
    private final RandomGeneratorFactory javaRandomProvider;

    public RandomSourceFromJavaRandom(RandomGeneratorFactory javaRandomProvider, long seed) {
        this.javaRandomProvider = javaRandomProvider;
        this.javaRandom = this.javaRandomProvider.create(seed);

    }

    public static PositionalRandomFactory positional(RandomGeneratorFactory rngProvider, long baseSeed) {
        return new PositionalRandomFactory() {
            @Override
            public RandomSource fromHashOf(String seedString) {
                var seed = baseSeed;
                for (var character : seedString.chars().toArray()) {
                    seed += character;
                    seed *= FibonacciHashing.GOLDEN_RATIO_LONG;
                }
                return new RandomSourceFromJavaRandom(rngProvider, seed);
            }

            @Override
            public RandomSource fromSeed(long seed) {
                return new RandomSourceFromJavaRandom(rngProvider, seed);
            }

            @Override
            public RandomSource at(int x, int y, int z) {
                var seed = baseSeed;
                seed += x;
                seed *= FibonacciHashing.GOLDEN_RATIO_LONG;
                seed += z;
                seed *= FibonacciHashing.GOLDEN_RATIO_LONG;
                seed += y;
                seed *= FibonacciHashing.GOLDEN_RATIO_LONG;
                return new RandomSourceFromJavaRandom(rngProvider, seed);
            }

            @Override
            public void parityConfigString(StringBuilder stringBuilder) {
                stringBuilder.append("JavaPositionalRNG{")
                        .append(rngProvider.name())
                        .append(", ")
                        .append(baseSeed)
                        .append("}");
            }
        };
    }

    public static RandomGeneratorFactory get(RandomFactoryType Factory) {
        return Factory.get();
    }

    @Override
    public RandomSource fork() {
        return new RandomSourceFromJavaRandom(javaRandomProvider, nextLong());
    }

    @Override
    public PositionalRandomFactory forkPositional() {
        return positional(javaRandomProvider, nextLong());
    }

    @Override
    public void setSeed(long l) {
        javaRandom = javaRandomProvider.create(l);
    }

    @Override
    public int nextInt() {
        return javaRandom.nextInt();
    }

    @Override
    public int nextInt(int i) {
        return javaRandom.nextInt(i);
    }

    @Override
    public long nextLong() {
        return javaRandom.nextLong();
    }

    @Override
    public boolean nextBoolean() {
        return javaRandom.nextBoolean();
    }

    @Override
    public float nextFloat() {
        return javaRandom.nextFloat();
    }

    @Override
    public double nextDouble() {
        return javaRandom.nextDouble();
    }

    @Override
    public double nextGaussian() {
        return javaRandom.nextGaussian();
    }

    public RandomGenerator getJavaRandom() {
        return this.javaRandom;
    }
}
