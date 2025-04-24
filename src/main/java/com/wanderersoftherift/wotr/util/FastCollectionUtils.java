package com.wanderersoftherift.wotr.util;

import it.unimi.dsi.fastutil.objects.Object2IntMap;

import javax.annotation.Nullable;

/**
 * Utility methods relating to the fastutil collection types
 */
public final class FastCollectionUtils {

    private FastCollectionUtils() {
    }

    /**
     * @param map
     * @return The key with the highest value in the map, or null if the map is empty
     * @param <T>
     */
    public static <T> @Nullable T max(Object2IntMap<T> map) {
        T result = null;
        int maxValue = 0;
        for (Object2IntMap.Entry<T> entry : map.object2IntEntrySet()) {
            if (entry.getIntValue() > maxValue) {
                maxValue = entry.getIntValue();
                result = entry.getKey();
            }
        }

        return result;
    }
}
