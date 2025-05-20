package com.wanderersoftherift.wotr.util;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;

// very basic non-thread-safe rng
// not great but good-enough, probably better than MOJANK LegacyRandomSource
public class FastRandomSource implements RandomSource {
    private static final long XOR_CONSTANT = 7987674492471257550L;
    private long seed;

    public FastRandomSource(long seed) {
        this.seed = seed;
    }

    @Override
    public RandomSource fork() {
        return new FastRandomSource(nextLong());
    }

    @Override
    public PositionalRandomFactory forkPositional() {
        return positional(nextLong());
    }

    public static PositionalRandomFactory positional(long baseSeed) {
        return new PositionalRandomFactory() {
            @Override
            public RandomSource fromHashOf(String seedString) {
                var seed = baseSeed;
                for (var character : seedString.chars().toArray()) {
                    seed += character;
                    seed *= FibonacciHashing.GOLDEN_RATIO_LONG;
                }
                return new FastRandomSource(seed);
            }

            @Override
            public RandomSource fromSeed(long seed) {
                return new FastRandomSource(seed);
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
                return new FastRandomSource(seed);
            }

            @Override
            public void parityConfigString(StringBuilder stringBuilder) {
                stringBuilder.append("FibonacciPositionalRNG{").append(baseSeed).append("}");
            }
        };
    }

    @Override
    public void setSeed(long l) {
        seed = l;
    }

    @Override
    public int nextInt() {
        return (int) (nextLong() >> 32);
    }

    @Override
    public int nextInt(int i) {
        return (int) Math.floorMod(nextLong(), (long) i);
    } //no idea how well this works

    @Override
    public long nextLong() {
        var generated = (FibonacciHashing.GOLDEN_RATIO_LONG * seed) ^ XOR_CONSTANT;
        seed += (generated >>> 55) + 1;
        return generated;
    }

    @Override
    public boolean nextBoolean() {
        return nextLong() >= 0;
    }

    @Override
    public float nextFloat() {
        var d = ((float) (nextLong() << 2));
        var dBits = Float.floatToIntBits(d);
        dBits-=64<<23;
        d = Float.intBitsToFloat(dBits);
        return d+0.5f;
    }

    @Override
    public double nextDouble() {
        var d = ((double) (nextLong() << 2));
        var dBits = Double.doubleToLongBits(d);
        dBits-=64L<<52;
        d = Double.longBitsToDouble(dBits);
        return d+0.5;
    }

    public double nextDoubleSigned() {
        var d = ((double) (nextLong() << 2));
        var dBits = Double.doubleToLongBits(d);
        dBits-=63L<<52;
        d = Double.longBitsToDouble(dBits);
        return d;
    }

    @Override
    public double nextGaussian() {
        var value = nextDoubleSigned();
        value = 0.5 * Math.log((1 + value) / (1 - value)) * 0.58; // atanh(x) * 0.58
        value = Math.log(value + Math.sqrt(value * value + 1)) * 2.14; // asinh(x) * 2.14
        return value;
    }
}
