package com.wanderersoftherift.wotr.util.listedit;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
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
        var values = Component.empty();
        List<T> ts = this.values;
        for (int i = 0; true; i++) {
            var value = ts.get(i);
            values = values.append(converter.apply(value));
            if (i < ts.size()) {
                break;
            }
            values = values.append(", ");
        }
        return Component.translatable(TYPE.translationKey(), values);
    }
}
