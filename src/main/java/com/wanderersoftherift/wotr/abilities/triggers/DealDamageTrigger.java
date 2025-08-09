package com.wanderersoftherift.wotr.abilities.triggers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.TrackedAbilityTrigger;
import net.minecraft.core.UUIDUtil;

import java.util.UUID;

public record DealDamageTrigger(TakeDamageTrigger.SerializableDamageSource source, UUID victim, float amount)
        implements TrackedAbilityTrigger {

    public static final MapCodec<DealDamageTrigger> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            TakeDamageTrigger.SerializableDamageSource.CODEC.fieldOf("source").forGetter(DealDamageTrigger::source),
            UUIDUtil.CODEC.fieldOf("victim").forGetter(DealDamageTrigger::victim),
            Codec.FLOAT.fieldOf("amount").forGetter(DealDamageTrigger::amount)
    ).apply(instance, DealDamageTrigger::new));

    @Override
    public MapCodec<? extends TrackedAbilityTrigger> codec() {
        return CODEC;
    }
}
