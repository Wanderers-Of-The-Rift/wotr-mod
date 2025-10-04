package com.wanderersoftherift.wotr.block.blockentity;

import net.minecraft.world.entity.LivingEntity;

public interface MobDeathNotifiable {

    void notifyOfDeath(LivingEntity entity);
}
