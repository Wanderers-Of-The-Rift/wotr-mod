package com.wanderersoftherift.wotr.util;

import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class RandomUtil {

    private RandomUtil() {
    }

    public static <T> List<T> randomSubset(Collection<T> set, int count, RandomSource random) {
        if (count > set.size()) {
            return List.copyOf(set);
        }
        if (set.size() - count > count) {
            List<T> choices = new ArrayList<>(set);
            List<T> result = new ArrayList<>(count);
            while (result.size() < count) {
                int choice = random.nextInt(choices.size());
                result.add(choices.get(choice));
                if (choice < result.size() - 1) {
                    choices.set(choice, choices.getLast());
                }
                choices.removeLast();
            }
            return result;
        } else {
            List<T> result = new ArrayList<>(set);
            while (result.size() > count) {
                int toRemove = random.nextInt(result.size());
                if (toRemove < result.size() - 1) {
                    result.set(toRemove, result.getLast());
                }
                result.removeLast();
            }
            return result;
        }
    }
}
