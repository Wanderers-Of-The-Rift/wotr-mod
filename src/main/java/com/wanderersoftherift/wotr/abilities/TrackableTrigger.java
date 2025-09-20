package com.wanderersoftherift.wotr.abilities;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.abilities.triggers.TriggerRegistry;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import net.minecraft.core.Holder;
import net.neoforged.neoforge.attachment.AttachmentType;

import javax.annotation.Nullable;
import java.util.function.Predicate;
import java.util.function.Supplier;

public interface TrackableTrigger {

    TriggerType<?> type();

    record TriggerType<T extends TrackableTrigger>(MapCodec<T> codec,
            MapCodec<? extends TriggerPredicate<T>> predicateCodec,
            @Nullable Supplier<AttachmentType<TriggerRegistry<T>>> registry) {
    }

    public interface TriggerPredicate<T extends TrackableTrigger> extends Predicate<T> {

        public static final Codec<TriggerPredicate<?>> CODEC = WotrRegistries.TRACKABLE_TRIGGERS.holderByNameCodec()
                .dispatch(TriggerPredicate::type, it -> it.value().predicateCodec);

        Holder<TriggerType<?>> type();

        boolean canBeHandledByClient();

    }
}
