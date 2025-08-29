package com.wanderersoftherift.wotr.util;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.Function;

public interface ListEdit<T> {

    static <T> Codec<ListEdit<T>> editCodec(Codec<T> codec) {
        return WotrRegistries.EDIT_TYPES.byNameCodec()
                .dispatch(ListEdit::type, it -> ((EditType<T>) it).codecSupplier.apply(codec));
    }

    List<T> apply(List<T> original);

    ListEdit.EditType<T> type();

    Component textComponent(Function<T, Component> converter);

    public record EditType<T>(Function<Codec<T>, MapCodec<? extends ListEdit<T>>> codecSupplier,
            String translationKey) {

        static <T> EditType<T> create(
                Function<Codec<T>, MapCodec<? extends ListEdit<T>>> codecSupplier,
                String translationCategory,
                String translationValue) {
            return new EditType<>(codecSupplier,
                    WanderersOfTheRift.translationId(translationCategory, translationValue));
        }
    }

    record Append<T>(List<T> values) implements ListEdit<T> {

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
            return Component.translatable(TYPE.translationKey, values);
        }
    }

    record Prepend<T>(List<T> values) implements ListEdit<T> {
        public static final EditType<?> TYPE = EditType.create(Prepend::createCodec, "edits", "prepend");

        public static <T> MapCodec<? extends ListEdit<T>> createCodec(Codec<T> element) {
            return element.listOf().xmap(Prepend<T>::new, Prepend<T>::values).fieldOf("values");
        }

        @Override
        public List<T> apply(List<T> original) {
            var newList = ImmutableList.<T>builder();
            newList.addAll(values());
            newList.addAll(original);
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
            return Component.translatable(TYPE.translationKey, values);
        }
    }

    record Clear<T>() implements ListEdit<T> {
        public static final Clear<?> INSTANCE = new Clear<>();
        public static final MapCodec<Clear<?>> CODEC = MapCodec.unit(INSTANCE);

        public static final EditType<?> TYPE = EditType.create(Clear::createCodec, "edits", "clear");

        public static <T> MapCodec<? extends ListEdit<T>> createCodec(Codec<T> element) {
            return (MapCodec<? extends ListEdit<T>>) (Object) CODEC;
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
            return Component.translatable(TYPE.translationKey);
        }
    }

    record Drop<T>(int n) implements ListEdit<T> {
        public static final MapCodec<Drop<Object>> CODEC = Codec.INT.xmap(Drop::new, Drop::n).fieldOf("count");

        public static final EditType<?> TYPE = EditType.create(Drop::createCodec, "edits", "drop");

        public static <T> MapCodec<? extends ListEdit<T>> createCodec(Codec<T> element) {
            return (MapCodec<? extends ListEdit<T>>) (Object) CODEC;
        }

        @Override
        public List<T> apply(List<T> original) {
            return original.subList(n, original.size() - 1);
        }

        @Override
        public EditType<T> type() {
            return (EditType<T>) TYPE;
        }

        @Override
        public Component textComponent(Function<T, Component> converter) {
            return Component.translatable(TYPE.translationKey, n);
        }
    }

    record DropLast<T>(int n) implements ListEdit<T> {
        public static final MapCodec<DropLast<Object>> CODEC = Codec.INT.xmap(DropLast::new, DropLast::n)
                .fieldOf("count");

        public static final EditType<?> TYPE = EditType.create(DropLast::createCodec, "edits", "drop_last");

        public static <T> MapCodec<? extends ListEdit<T>> createCodec(Codec<T> element) {
            return (MapCodec<? extends ListEdit<T>>) (Object) CODEC;
        }

        @Override
        public List<T> apply(List<T> original) {
            return original.subList(0, original.size() - 1 - n);
        }

        @Override
        public EditType<T> type() {
            return (EditType<T>) TYPE;
        }

        @Override
        public Component textComponent(Function<T, Component> converter) {
            return Component.translatable(TYPE.translationKey, n);
        }
    }
}
