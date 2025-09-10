package com.wanderersoftherift.wotr.util.listedit;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.util.ComponentUtil;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.Function;

public record Append<T>(List<T> values) implements ListEdit<T> {

    public static final EditType<?> TYPE = EditType.create(Append::createCodec, "edits", "append");

    private static <T> MapCodec<? extends ListEdit<T>> createCodec(Codec<T> element) {
        return element.listOf().xmap(Append<T>::new, Append<T>::values).fieldOf("values");
    }

    @Override
    public List<T> apply(List<T> original) {
        var newList = ImmutableList.<T>builder();
        newList.addAll(original);
        newList.addAll(values());
        return newList.build();
    }

    @Override
    public EditType<T> type() {
        return (EditType<T>) TYPE;
    }

    @Override
    public Component textComponent(Function<T, Component> converter) {
        var jointValues = this.values.stream()
                .map(converter)
                .reduce((a, b) -> ComponentUtil.mutable(a).append(", ").append(b));
        return Component.translatable(TYPE.translationKey(), jointValues.orElse(Component.empty()));
    }
}
