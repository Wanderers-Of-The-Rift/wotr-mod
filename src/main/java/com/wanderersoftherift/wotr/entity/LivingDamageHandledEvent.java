package com.wanderersoftherift.wotr.entity;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.living.LivingEvent;

public class LivingDamageHandledEvent extends LivingEvent {
    private final DamageSource source;

    public LivingDamageHandledEvent(LivingEntity entity, DamageSource source) {
        super(entity);
        this.source = source;
    }

    public DamageSource getSource() {
        return source;
    }
}
