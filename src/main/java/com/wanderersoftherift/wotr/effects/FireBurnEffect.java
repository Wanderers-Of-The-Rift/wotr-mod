package com.wanderersoftherift.wotr.effects;

import com.wanderersoftherift.wotr.init.ModDamageTypes;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class FireBurnEffect extends MobEffect {
    public FireBurnEffect() {
        super(MobEffectCategory.HARMFUL, 0xFFFF00);
    }

    @Override
    public boolean applyEffectTick(ServerLevel level, LivingEntity entity, int amplifier) {
        if (level.isClientSide()) {
            return true;
        }

        Holder<DamageType> damageType = level.registryAccess()
                .lookupOrThrow(Registries.DAMAGE_TYPE)
                .getOrThrow(ModDamageTypes.FIRE_BURN_DAMAGE);
        DamageSource damageSource = new DamageSource(damageType);

        entity.hurtServer(level, damageSource, 2);
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }
}
