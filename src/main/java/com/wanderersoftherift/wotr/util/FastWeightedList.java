package com.wanderersoftherift.wotr.util;

import com.google.common.collect.ImmutableList;
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

    public static <T> FastWeightedList<T> of(Pair<Float,T>... entries){
        var sortedEntries = Arrays.stream(entries).sorted(Comparator.comparingDouble(it -> -it.getA())).toList();

        var values = new ArrayList<T>(sortedEntries.size());
        var weights = new FloatArrayList(sortedEntries.size());
        for (Pair<Float, T> sortedEntry : sortedEntries) {
            values.add(sortedEntry.getB());
            weights.add(sortedEntry.getA());
        }
        return new FastWeightedList<>(ImmutableList.copyOf(values),new FloatImmutableList(weights));

    }

    public static <T> FastWeightedList<T> merge(FastWeightedList<T>... lists){
        return of(Arrays.stream(lists).flatMap(it -> it.entries()).toArray(Pair[]::new));
    }

    public static <T> FastWeightedList<T> byCountingDuplicates(List<T> entries, Function<T,Object> keyComputation){

        var counted = new HashMap<Object,Pair<T,Integer>>();
        for (var entry : entries){
            var key = keyComputation.apply(entry);
            if (key!=null) {
                counted.compute(key, (sid, old) -> new Pair<>(entry, old == null ? 1 : old.getB() + 1));
            }
        }
        return FastWeightedList.of(counted.entrySet().stream().map(entry->new Pair((float)(int) entry.getValue().getB(),entry.getValue().getA())).toArray((size)->new Pair[size]));
    }

    private Stream<Pair<Float, T>> entries() {
        return IntStream.range(0, weights.size()).mapToObj(idx -> new Pair<>(weights.getFloat(idx), values.get(idx)));
    }

    public T random(RandomSource rng){
        var selected = rng.nextDouble()*totalWeight;
        for (int i = 0; i < weights.size(); i++) {
            var weight = weights.getFloat(i);
            if(weight > selected) return values.get(i);
            selected -= weight;
        }
        return null;
    }
}
