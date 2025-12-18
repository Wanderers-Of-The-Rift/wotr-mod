package com.wanderersoftherift.wotr.util;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multiset;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Multimap of type instances keyed by type
 * 
 * @param <T>
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class TypeListMultimap<T> {
    private final ListMultimap contents = ArrayListMultimap.create();

    public <U extends T> List<U> get(Class<U> key) {
        return (List<U>) contents.get(key);
    }

    public @NotNull Set<Class<? extends T>> keySet() {
        return contents.keySet();
    }

    public @NotNull Multiset<Class<? extends T>> keys() {
        return contents.keys();
    }

    public @NotNull Collection<? extends T> values() {
        return contents.values();
    }

    public int size() {
        return contents.size();
    }

    public boolean isEmpty() {
        return contents.isEmpty();
    }

    public boolean containsKey(@Nullable Class<? extends T> key) {
        return contents.containsKey(key);
    }

    public boolean containsValue(@Nullable T value) {
        return contents.containsValue(value);
    }

    public <U extends T> boolean containsEntry(@Nullable Class<U> key, @Nullable U value) {
        return contents.containsEntry(key, value);
    }

    public <U extends T> boolean remove(@Nullable Class<U> key, @Nullable U value) {
        return contents.remove(key, value);
    }

    public <U extends T> boolean putAll(Class<U> key, Iterable<U> values) {
        return contents.putAll(key, values);
    }

    public <U extends T> boolean put(U value) {
        return contents.put(value.getClass(), value);
    }

    public <U extends T> boolean put(Class<U> key, U value) {
        return contents.put(key, value);
    }

    public <U extends T> List<U> removeAll(@Nullable Class<U> key) {
        return contents.removeAll(key);
    }

    public void clear() {
        contents.clear();
    }

    public Map<Class<? extends T>, Collection<? extends T>> asMap() {
        return contents.asMap();
    }

    public <U extends T> List<U> replaceValues(Class<U> key, Iterable<U> values) {
        return contents.replaceValues(key, values);
    }

    public Collection<Map.Entry<Class<? extends T>, ? extends T>> entries() {
        return contents.entries();
    }
}
