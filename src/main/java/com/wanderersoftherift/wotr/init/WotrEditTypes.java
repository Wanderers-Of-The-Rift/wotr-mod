package com.wanderersoftherift.wotr.init;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.util.listedit.Append;
import com.wanderersoftherift.wotr.util.listedit.Clear;
import com.wanderersoftherift.wotr.util.listedit.Drop;
import com.wanderersoftherift.wotr.util.listedit.DropLast;
import com.wanderersoftherift.wotr.util.listedit.EditType;
import com.wanderersoftherift.wotr.util.listedit.Prepend;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class WotrEditTypes {
    public static final DeferredRegister<EditType<?>> EDIT_TYPES = DeferredRegister.create(WotrRegistries.EDIT_TYPES,
            WanderersOfTheRift.MODID);

    public static final DeferredHolder<EditType<?>, EditType<?>> APPEND = EDIT_TYPES.register("append",
            () -> Append.TYPE);

    public static final DeferredHolder<EditType<?>, EditType<?>> PREPEND = EDIT_TYPES.register("prepend",
            () -> Prepend.TYPE);

    public static final DeferredHolder<EditType<?>, EditType<?>> CLEAR = EDIT_TYPES.register("clear", () -> Clear.TYPE);

    public static final DeferredHolder<EditType<?>, EditType<?>> DROP = EDIT_TYPES.register("drop", () -> Drop.TYPE);

    public static final DeferredHolder<EditType<?>, EditType<?>> DROP_LAST = EDIT_TYPES.register("drop_lase",
            () -> DropLast.TYPE);
}
