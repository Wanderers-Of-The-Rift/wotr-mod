package com.wanderersoftherift.wotr.serialization;

import com.mojang.serialization.Codec;

import java.util.HashMap;
import java.util.Map;

public class MutableMapCodec {
    public static <K, V> Codec<Map<K, V>> of(Codec<Map<K, V>> original) {
        return original.xmap(HashMap::new, it -> it);
    }
}
