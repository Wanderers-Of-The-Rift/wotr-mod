package com.wanderersoftherift.wotr.util.listedit;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.Function;

public record Clear<T>() implements ListEdit<T> {
    public static final EditType<?> TYPE = EditType.create(Clear::createCodec, "edits", "clear");
    private static final Clear<?> INSTANCE = new Clear<>();
    private static final MapCodec<Clear<?>> CODEC = MapCodec.unit(INSTANCE);

    public static <T> MapCodec<? extends ListEdit<T>> createCodec(Codec<T> element) {
        return (MapCodec<? extends ListEdit<T>>) (Object) CODEC;
    }

    public static <T> Clear<T> instance() {
        return (Clear<T>) INSTANCE;
    }

    @Override
    public List<T> apply(List<T> original) {
        return ImmutableList.of();
    }

    @Override
    public EditType<T> type() {
        return (EditType<T>) TYPE;
    }

    @Override
    public Component textComponent(Function<T, Component> converter) {
        return Component.translatable(TYPE.translationKey());
    }
}
