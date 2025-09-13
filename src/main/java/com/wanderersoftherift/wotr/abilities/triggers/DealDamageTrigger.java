package com.wanderersoftherift.wotr.abilities.triggers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.TrackableTrigger;
import com.wanderersoftherift.wotr.util.SerializableDamageSource;
import net.minecraft.core.UUIDUtil;

import java.util.UUID;

public record DealDamageTrigger(SerializableDamageSource source, UUID victim, float amount)
        implements TrackableTrigger {

    private static final MapCodec<DealDamageTrigger> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            SerializableDamageSource.CODEC.fieldOf("source").forGetter(DealDamageTrigger::source),
            UUIDUtil.CODEC.fieldOf("victim").forGetter(DealDamageTrigger::victim),
            Codec.FLOAT.fieldOf("amount").forGetter(DealDamageTrigger::amount)
    ).apply(instance, DealDamageTrigger::new));

    public static final TriggerType TRIGGER_TYPE = new TriggerType<>(CODEC, null);

    @Override
    public TriggerType<?> type() {
        return TRIGGER_TYPE;
    }
}
