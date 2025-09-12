package com.wanderersoftherift.wotr.abilities.triggers;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.TrackableTrigger;

public record LootTrigger() implements TrackableTrigger {
    public static final LootTrigger INSTANCE = new LootTrigger();
    private static final MapCodec<LootTrigger> CODEC = RecordCodecBuilder
            .mapCodec(instance -> instance.point(INSTANCE));
    public static final TriggerType TRIGGER_TYPE = new TriggerType<>(CODEC, null);

    @Override
    public TriggerType<?> type() {
        return TRIGGER_TYPE;
    }
}
