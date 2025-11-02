package com.wanderersoftherift.wotr.util;

import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class RandomUtil {

    private RandomUtil() {
    }

    /**
     * Created a random subset of a given collection
     * 
     * @param input  The full set
     * @param count  The number of items to return
     * @param random A random source
     * @return A list containing the lesser of input.size() and count items, randomly selected
     * @param <T>
     */
    public static <T> List<T> randomSubset(Collection<T> input, int count, RandomSource random) {
        if (count >= input.size()) {
            return List.copyOf(input);
        }
        // Select a subset strategy to minimise steps required
        if (input.size() - count > count) {
            return selectUpToCount(input, count, random);
        } else {
            return subsetDownToCount(input, count, random);
        }
    }

    /**
     * With this strategy we start with a full list and remove items down to the desired count
     */
    private static <T> @NotNull List<T> subsetDownToCount(Collection<T> input, int count, RandomSource random) {
        List<T> result = new ArrayList<>(input);
        while (result.size() > count) {
            int toRemove = random.nextInt(result.size());
            if (toRemove < result.size() - 1) {
                result.set(toRemove, result.getLast());
            }
            result.removeLast();
        }
        return result;
    }

    /**
     * With this strategy we select items from the input up to the desired count.
     */
    private static <T> @NotNull List<T> selectUpToCount(Collection<T> input, int count, RandomSource random) {
        List<T> choices = new ArrayList<>(input);
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
    }
}
