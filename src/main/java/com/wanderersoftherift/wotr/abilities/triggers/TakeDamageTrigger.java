package com.wanderersoftherift.wotr.abilities.triggers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.TrackedAbilityTrigger;
import com.wanderersoftherift.wotr.util.SerializableDamageSource;

public record TakeDamageTrigger(SerializableDamageSource source, float amount) implements TrackedAbilityTrigger {
    private static final MapCodec<TakeDamageTrigger> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            SerializableDamageSource.CODEC.fieldOf("source").forGetter(TakeDamageTrigger::source),
            Codec.FLOAT.fieldOf("amount").forGetter(TakeDamageTrigger::amount)
    ).apply(instance, TakeDamageTrigger::new));

    public static final TriggerType TRIGGER_TYPE = new TriggerType(CODEC, null);

    @Override
    public TriggerType type() {
        return TRIGGER_TYPE;
    }

}
