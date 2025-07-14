package com.wanderersoftherift.wotr.serialization;

import com.mojang.serialization.Codec;

import java.util.ArrayList;
import java.util.List;

public class MutableListCodec {
    public static <T> Codec<List<T>> of(Codec<T> element) {
        return element.listOf().xmap(it -> new ArrayList<>(it), it -> it);
    }
}
