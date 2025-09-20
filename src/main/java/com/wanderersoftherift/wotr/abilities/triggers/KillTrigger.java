package com.wanderersoftherift.wotr.abilities.triggers;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.TrackableTrigger;
import com.wanderersoftherift.wotr.init.ability.WotrTrackedAbilityTriggers;
import com.wanderersoftherift.wotr.util.SerializableDamageSource;
import net.minecraft.core.Holder;
import net.minecraft.core.UUIDUtil;

import java.util.UUID;

public record KillTrigger(SerializableDamageSource source, UUID victim) implements TrackableTrigger {

    private static final MapCodec<KillTrigger> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            SerializableDamageSource.CODEC.fieldOf("source").forGetter(KillTrigger::source),
            UUIDUtil.CODEC.fieldOf("victim").forGetter(KillTrigger::victim)
    ).apply(instance, KillTrigger::new));

    public static final TriggerType<KillTrigger> TRIGGER_TYPE = new TriggerType<>(CODEC, KillPredicate.CODEC, null);

    @Override
    public TriggerType<?> type() {
        return TRIGGER_TYPE;
    }

    public record KillPredicate() implements TriggerPredicate<KillTrigger> {
        public static final MapCodec<KillPredicate> CODEC = MapCodec.unit(new KillPredicate());

        @Override
        public Holder<TriggerType<?>> type() {
            return WotrTrackedAbilityTriggers.KILL.getDelegate();
        }

        @Override
        public boolean canBeHandledByClient() {
            return true;
        }

        @Override
        public boolean test(KillTrigger trigger) {
            return true;
        }
    }

}
