package com.wanderersoftherift.wotr.mixin;

import net.minecraft.world.level.block.entity.TrialSpawnerBlockEntity;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TrialSpawnerBlockEntity.class)
public interface TrialSpawnerBlockEntityAccessor {

    @Accessor("trialSpawner")
    void setTrialSpawner(TrialSpawner trialSpawner);
}
