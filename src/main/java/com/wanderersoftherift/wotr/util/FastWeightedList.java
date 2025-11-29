package com.wanderersoftherift.wotr.util;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatImmutableList;
import net.minecraft.util.RandomSource;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class FastWeightedList<T> {
    private final ImmutableList<T> values;
    private final FloatImmutableList weights;
    private final float totalWeight;

    private FastWeightedList(ImmutableList<T> values, FloatImmutableList weights) {
        this.values = values;
        this.weights = weights;
        this.totalWeight = (float) weights.doubleStream().sum();
    }

    public static <T> Codec<FastWeightedList<T>> codec(Codec<T> valueCodec) {
        return Codec.unboundedMap(valueCodec, Codec.FLOAT)
                .xmap(map -> of(
                        map.entrySet().stream().map(it -> new Pair<>(it.getValue(), it.getKey())).toArray(Pair[]::new)),
                        list -> {
                            var result = new HashMap<T, Float>();
                            for (int i = 0; i < list.weights.size(); i++) {
                                result.put(list.values.get(i), list.weights.getFloat(i));
                            }
                            return result;
                        });
    }

    public static <T> Codec<FastWeightedList<T>> listCodec(Codec<T> valueCodec) {
        return RecordCodecBuilder.<Pair<Float, T>>create(
                instance -> instance
                        .group(Codec.FLOAT.fieldOf("weight").forGetter(Pair::getA),
                                valueCodec.fieldOf("value").forGetter(Pair::getB))
                        .apply(instance, Pair::new))
                .listOf()
                .xmap(it -> of(it.<Pair<Float, T>>toArray(Pair[]::new)), it -> it.entries().toList());
    }

    public static <T> Codec<FastWeightedList<T>> codecWithSingleAlternative(Codec<T> valueCodec) {
        return Codec.withAlternative(FastWeightedList.codec(valueCodec), valueCodec,
                single -> FastWeightedList.of(new Pair<>(1f, single)));
    }

    public static <T> FastWeightedList<T> of(Pair<Float, T>... entries) {
        var sortedEntries = Arrays.stream(entries)
                .sorted(Comparator.comparingDouble(Pair<Float, T>::getA).reversed())
                .toList();

        var values = new ArrayList<T>(sortedEntries.size());
        var weights = new FloatArrayList(sortedEntries.size());
        for (Pair<Float, T> sortedEntry : sortedEntries) {
            values.add(sortedEntry.getB());
            weights.add(sortedEntry.getA());
        }
        return new FastWeightedList<>(ImmutableList.copyOf(values), new FloatImmutableList(weights));

    }

    public static <T> FastWeightedList<T> merge(FastWeightedList<T>... lists) {
        return of(Arrays.stream(lists).flatMap(FastWeightedList::entries).toArray(Pair[]::new));
    }

    public static <T, K extends Comparable<K>> FastWeightedList<T> byCountingDuplicates(
            List<T> entries,
            Function<T, K> keyComputation) {

        var counted = new HashMap<Object, Pair<T, Integer>>();
        for (var entry : entries) {
            var key = keyComputation.apply(entry);
            if (key != null) {
                counted.compute(key, (sid, old) -> new Pair<>(entry, old == null ? 1 : old.getB() + 1));
            }
        }
        return FastWeightedList.of(counted.entrySet()
                .stream()
                .sorted((a, b) -> ((Comparable) a.getKey()).compareTo(b.getKey()))
                .map(entry -> new Pair((float) (int) entry.getValue().getB(), entry.getValue().getA()))
                .toArray((size) -> new Pair[size]));
    }

    private Stream<Pair<Float, T>> entries() {
        return IntStream.range(0, weights.size()).mapToObj(idx -> new Pair<>(weights.getFloat(idx), values.get(idx)));
    }

    public T random(RandomSource rng) {
        if (weights.isEmpty()) {
            return null;
        }
        if (weights.size() == 1) {
            return values.getFirst();
        }
        return forRoll(rng.nextFloat());
    }

    /**
     * @param roll A random number between 0 and 1
     * @return
     */
    public T forRoll(float roll) {
        var selected = roll * totalWeight;
        for (int i = 0; i < weights.size(); i++) {
            var weight = weights.getFloat(i);
            if (weight > selected) {
                return values.get(i);
            }
            selected -= weight;
        }
        return null;
    }

    public boolean isEmpty() {
        return totalWeight == 0f || weights.isEmpty();
    }
}
