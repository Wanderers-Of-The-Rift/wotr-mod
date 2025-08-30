package com.wanderersoftherift.wotr.util.listedit;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.WanderersOfTheRift;

import java.util.function.Function;

public record EditType<T>(Function<Codec<T>, MapCodec<? extends ListEdit<T>>> codecSupplier, String translationKey) {

    static <T> EditType<T> create(
            Function<Codec<T>, MapCodec<? extends ListEdit<T>>> codecSupplier,
            String translationCategory,
            String translationValue) {
        return new EditType<>(codecSupplier, WanderersOfTheRift.translationId(translationCategory, translationValue));
    }
}
