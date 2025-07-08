package com.wanderersoftherift.wotr.serialization;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

import java.util.function.Function;

/**
 * Codec for a pair where the Codec for the second value is determined by the first value
 * 
 * @param keyCodec           The codec for the key, which should have field specified
 * @param valueField         The field for the value
 * @param valueCodecFunction The function for obtaining the value codec from the key
 * @param <K>                Type of the key
 * @param <V>                Type of the value
 */
public record DispatchedPair<K, V>(Codec<K> keyCodec, String valueField, Function<K, Codec<V>> valueCodecFunction)
        implements Codec<Pair<K, V>> {

    @Override
    public <T> DataResult<T> encode(final Pair<K, V> input, final DynamicOps<T> ops, final T rest) {
        Codec<V> valueCodec = valueCodecFunction.apply(input.getFirst()).fieldOf(valueField).codec();
        return valueCodec.encode(input.getSecond(), ops, rest).flatMap(f -> keyCodec.encode(input.getFirst(), ops, f));
    }

    @Override
    public <T> DataResult<Pair<Pair<K, V>, T>> decode(final DynamicOps<T> ops, final T input) {
        return keyCodec.decode(ops, input).flatMap(p1 -> {
            Codec<V> valueCodec = valueCodecFunction.apply(p1.getFirst()).fieldOf(valueField).codec();
            return valueCodec.decode(ops, p1.getSecond())
                    .map(p2 -> Pair.of(Pair.of(p1.getFirst(), p2.getFirst()), p2.getSecond())
                    );
        });
    }

}
