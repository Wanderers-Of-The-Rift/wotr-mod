package com.wanderersoftherift.wotr.abilities.triggers;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.TrackableTrigger;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.init.ability.WotrTrackedAbilityTriggers;
import net.minecraft.core.Holder;

public record TickTrigger() implements TrackableTrigger {
    public static final TickTrigger INSTANCE = new TickTrigger();
    private static final MapCodec<TickTrigger> CODEC = RecordCodecBuilder
            .mapCodec(instance -> instance.point(INSTANCE));
    public static final TriggerType<TickTrigger> TRIGGER_TYPE = new TriggerType<>(CODEC, TickPredicate.CODEC,
            WotrAttachments.TICK_TRIGGER_REGISTRY);

    @Override
    public TriggerType<?> type() {
        return TRIGGER_TYPE;
    }

    public record TickPredicate() implements TriggerPredicate<TickTrigger> {
        public static final MapCodec<TickPredicate> CODEC = MapCodec.unit(new TickPredicate());

        @Override
        public Holder<TriggerType<?>> type() {
            return WotrTrackedAbilityTriggers.TICK_TRIGGER.getDelegate();
        }

        @Override
        public boolean canBeHandledByClient() {
            return true;
        }

        @Override
        public boolean test(TickTrigger trigger) {
            return true;
        }
    }
}
