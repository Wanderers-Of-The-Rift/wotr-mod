package com.wanderersoftherift.wotr.init;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.util.ListEdit;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class WotrEditTypes {
    public static final DeferredRegister<ListEdit.EditType<?>> EDIT_TYPES = DeferredRegister
            .create(WotrRegistries.EDIT_TYPES, WanderersOfTheRift.MODID);

    public static final DeferredHolder<ListEdit.EditType<?>, ListEdit.EditType<?>> APPEND = EDIT_TYPES
            .register("append", () -> ListEdit.Append.TYPE);

    public static final DeferredHolder<ListEdit.EditType<?>, ListEdit.EditType<?>> PREPEND = EDIT_TYPES
            .register("prepend", () -> ListEdit.Prepend.TYPE);

    public static final DeferredHolder<ListEdit.EditType<?>, ListEdit.EditType<?>> CLEAR = EDIT_TYPES.register("clear",
            () -> ListEdit.Clear.TYPE);

    public static final DeferredHolder<ListEdit.EditType<?>, ListEdit.EditType<?>> DROP = EDIT_TYPES.register("drop",
            () -> ListEdit.Drop.TYPE);

    public static final DeferredHolder<ListEdit.EditType<?>, ListEdit.EditType<?>> DROP_LAST = EDIT_TYPES
            .register("drop_lase", () -> ListEdit.DropLast.TYPE);
}
