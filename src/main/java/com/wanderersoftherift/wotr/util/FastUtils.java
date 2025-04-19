package com.wanderersoftherift.wotr.util;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.Object2IntMap;

import java.util.List;
import java.util.Map;

public final class FastUtils {

    public static <T> Map<T, Integer> toMap(Object2IntMap<T> source) {
        ImmutableMap.Builder<T, Integer> builder = ImmutableMap.builder();
        for (Object2IntMap.Entry<T> entry : source.object2IntEntrySet()) {
            builder.put(entry.getKey(), entry.getIntValue());
        }
        return builder.build();
    }

    public static List<Integer> toList(IntList list) {
        return list.intStream().boxed().toList();
    }

    private FastUtils() {
    }
}
