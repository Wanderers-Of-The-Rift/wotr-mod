package com.wanderersoftherift.wotr.abilities.triggers;

import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.attachment.AttachmentType;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public interface TrackableTrigger {

    TriggerType<?> type();

    record TriggerType<T extends TrackableTrigger>(MapCodec<? extends TriggerPredicate<T>> predicateCodec,
            @Nullable Supplier<AttachmentType<TriggerRegistry<T>>> registry) {
    }

}
