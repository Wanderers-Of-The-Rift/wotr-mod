package com.wanderersoftherift.wotr.abilities.triggers;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.TrackedAbilityTrigger;
import com.wanderersoftherift.wotr.util.SerializableDamageSource;
import net.minecraft.core.UUIDUtil;

import java.util.UUID;

public record KillTrigger(SerializableDamageSource source, UUID victim) implements TrackedAbilityTrigger {

    private static final MapCodec<KillTrigger> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            SerializableDamageSource.CODEC.fieldOf("source").forGetter(KillTrigger::source),
            UUIDUtil.CODEC.fieldOf("victim").forGetter(KillTrigger::victim)
    ).apply(instance, KillTrigger::new));

    public static final TriggerType TRIGGER_TYPE = new TriggerType(CODEC, null);

    @Override
    public TriggerType type() {
        return TRIGGER_TYPE;
    }
}
