package com.wanderersoftherift.wotr.util.listedit;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.Function;

public record Drop<T>(int n) implements ListEdit<T> {
    public static final MapCodec<Drop<Object>> CODEC = Codec.INT.xmap(Drop::new, Drop::n).fieldOf("count");

    public static final EditType<?> TYPE = EditType.create(Drop::createCodec, "edits", "drop");

    public static <T> MapCodec<? extends ListEdit<T>> createCodec(Codec<T> element) {
        return (MapCodec<? extends ListEdit<T>>) (Object) CODEC;
    }

    @Override
    public List<T> apply(List<T> original) {
        return original.subList(n, original.size());
    }

    @Override
    public EditType<T> type() {
        return (EditType<T>) TYPE;
    }

    @Override
    public Component textComponent(Function<T, Component> converter) {
        return Component.translatable(TYPE.translationKey(), n);
    }
}
