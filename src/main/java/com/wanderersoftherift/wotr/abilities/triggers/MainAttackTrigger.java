package com.wanderersoftherift.wotr.abilities.triggers;

public class MainAttackTrigger implements TrackableTrigger {
    public static final MainAttackTrigger INSTANCE = new MainAttackTrigger();
    public static final TriggerType<MainAttackTrigger> TYPE = new TrackableTrigger.TriggerType<>(
            MainTriggerPredicate.CODEC, null, INSTANCE);

    @Override
    public TriggerType<?> type() {
        return TYPE;
    }
}
