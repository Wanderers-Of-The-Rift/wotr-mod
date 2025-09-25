package com.wanderersoftherift.wotr.abilities.triggers;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.init.WotrAttachments;

public record TickTrigger() implements TrackableTrigger {
    public static final TickTrigger INSTANCE = new TickTrigger();
    private static final MapCodec<TickTrigger> CODEC = RecordCodecBuilder
            .mapCodec(instance -> instance.point(INSTANCE));
    public static final TriggerType<TickTrigger> TRIGGER_TYPE = new TriggerType<>(TickPredicate.CODEC,
            WotrAttachments.TICK_TRIGGER_REGISTRY);

    @Override
    public TriggerType<?> type() {
        return TRIGGER_TYPE;
    }

}
