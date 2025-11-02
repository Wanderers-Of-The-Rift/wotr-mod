package com.wanderersoftherift.wotr.serialization;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;
import com.mojang.serialization.Codec;

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
     * @param <K>        The type of the key.
     * @param <V>        The type of the value.
     */
    public static <K, V> Codec<ListMultimap<K, V>> unboundListMultimap(Codec<K> keyCodec, Codec<V> valueCodec) {
        return Codec.unboundedMap(keyCodec, valueCodec.listOf()).xmap(x -> {
            ListMultimap<K, V> result = ArrayListMultimap.create(x.size(), 3);
            x.forEach(result::putAll);
            return result;
        }, Multimaps::asMap);
    }
}
