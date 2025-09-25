package com.wanderersoftherift.wotr.abilities.triggers;

import com.mojang.serialization.Codec;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import net.minecraft.core.Holder;

import java.util.function.Predicate;

public interface TriggerPredicate<T extends TrackableTrigger> extends Predicate<T> {

    public static final Codec<TriggerPredicate<?>> CODEC = WotrRegistries.TRACKABLE_TRIGGERS.holderByNameCodec()
            .dispatch(TriggerPredicate::type, it -> it.value().predicateCodec());

    Holder<TrackableTrigger.TriggerType<?>> type();

    boolean canBeHandledByClient();

}
