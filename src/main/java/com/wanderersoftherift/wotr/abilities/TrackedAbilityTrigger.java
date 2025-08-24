package com.wanderersoftherift.wotr.abilities;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.abilities.triggers.TriggerRegistry;
import net.neoforged.neoforge.attachment.AttachmentType;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public interface TrackedAbilityTrigger {

    TriggerType type();

    record TriggerType<T extends TrackedAbilityTrigger>(MapCodec<T> codec,
            @Nullable Supplier<AttachmentType<TriggerRegistry<T>>> registry) {
    }
}
