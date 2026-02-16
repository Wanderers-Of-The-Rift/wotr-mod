package com.wanderersoftherift.wotr.effects;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class KillEffect extends MobEffect {
    public KillEffect() {
        super(MobEffectCategory.HARMFUL, 0x2b2b2b);
    }

    @Override
    public boolean applyEffectTick(ServerLevel level, LivingEntity entity, int amplifier) {
        if (level.isClientSide()) {
            return true;
        }

        entity.hurtServer(level, entity.damageSources().genericKill(), Float.MAX_VALUE);
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration == 1;
    }

}
