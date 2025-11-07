package com.wanderersoftherift.wotr.serialization;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;

import java.util.List;
import java.util.Map;

/**
 * Codecs for guava collections
 */
public final class GuavaCodecs {

    private GuavaCodecs() {
    }

    /**
     * Codec for a guava ListMultimap, where the key is string-serializable.
     * 
     * @param keyCodec   Codec for serializing the key. Must serialize to a string
     * @param valueCodec Codec for serializing the value
     * @return
     * @param <K> The type of the key.
     * @param <V> The type of the value.
     */
    public static <K, V> Codec<ListMultimap<K, V>> stringKeyListMultimap(Codec<K> keyCodec, Codec<V> valueCodec) {
        return Codec.unboundedMap(keyCodec, valueCodec.listOf()).xmap(x -> {
            ListMultimap<K, V> result = ArrayListMultimap.create(x.size(), 3);
            x.forEach(result::putAll);
            return result;
        }, Multimaps::asMap);
    }

    /**
     * Codec for a guava ListMultimap, where the key is not string serializable.
     * 
     * @param keyCodec   Codec for serializing the key
     * @param valueCodec Codec for serializing the value
     * @return
     * @param <K> The type of the key.
     * @param <V> The type of the value.
     */
    public static <K, V> Codec<ListMultimap<K, V>> compoundKeyListMultimap(Codec<K> keyCodec, Codec<V> valueCodec) {
        return Codec.pair(keyCodec, valueCodec.listOf()).listOf().xmap(pairList -> {
            ListMultimap<K, V> result = ArrayListMultimap.create(pairList.size(), 3);
            pairList.forEach(pair -> result.putAll(pair.getFirst(), pair.getSecond()));
            return result;
        }, multimap -> {
            Map<K, List<V>> map = Multimaps.asMap(multimap);
            return map.entrySet().stream().map(entry -> new Pair<>(entry.getKey(), entry.getValue())).toList();
        });
    }
}
