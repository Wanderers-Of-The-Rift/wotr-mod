package com.wanderersoftherift.wotr.abilities.triggers;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.TrackedAbilityTrigger;
import com.wanderersoftherift.wotr.init.WotrAttachments;

public record TickTrigger() implements TrackedAbilityTrigger {
    public static final TickTrigger INSTANCE = new TickTrigger();
    private static final MapCodec<TickTrigger> CODEC = RecordCodecBuilder
            .mapCodec(instance -> instance.point(INSTANCE));
    public static final Type TYPE = new Type(CODEC, WotrAttachments.TICK_TRIGGER_REGISTRY);

    @Override
    public Type type() {
        return TYPE;
    }
}
