package com.wanderersoftherift.wotr.util.listedit;

import com.mojang.serialization.Codec;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.Function;

public interface ListEdit<T> {

    static <T> Codec<ListEdit<T>> editCodec(Codec<T> codec) {
        return WotrRegistries.EDIT_TYPES.byNameCodec()
                .dispatch(ListEdit::type, it -> ((EditType<T>) it).codecSupplier().apply(codec));
    }

    List<T> apply(List<T> original);

    EditType<T> type();

    Component textComponent(Function<T, Component> converter);

}
