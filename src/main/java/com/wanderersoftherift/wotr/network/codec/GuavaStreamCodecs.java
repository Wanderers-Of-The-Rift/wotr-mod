package com.wanderersoftherift.wotr.network.codec;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public final class GuavaStreamCodecs {

    private GuavaStreamCodecs() {
    }

    /**
     * @param keyCodec   Codec for serializing the key
     * @param valueCodec Codec for serializing a list of value
     * @return A codec for serializing a list multimap for the given key and value types
     * @param <B> The ByteBuf required
     * @param <K> The key type
     * @param <V> The value type
     */
    public static <B extends ByteBuf, K, V> StreamCodec<B, ListMultimap<K, V>> listMultimap(
            StreamCodec<? super B, K> keyCodec,
            StreamCodec<? super B, List<V>> valueCodec) {

        return new StreamCodec<>() {
            public void encode(@NotNull B buffer, @NotNull ListMultimap<K, V> multimap) {
                Map<K, List<V>> map = Multimaps.asMap(multimap);
                ByteBufCodecs.writeCount(buffer, map.size(), Integer.MAX_VALUE);
                map.forEach((key, valueList) -> {
                    keyCodec.encode(buffer, key);
                    valueCodec.encode(buffer, valueList);
                });
            }

            public @NotNull ListMultimap<K, V> decode(@NotNull B buffer) {
                int size = ByteBufCodecs.readCount(buffer, Integer.MAX_VALUE);
                ListMultimap<K, V> result = ArrayListMultimap.create(size, 3);

                for (int j = 0; j < size; j++) {
                    K k = keyCodec.decode(buffer);
                    List<V> v = valueCodec.decode(buffer);
                    result.putAll(k, v);
                }

                return result;
            }
        };
    }
}
