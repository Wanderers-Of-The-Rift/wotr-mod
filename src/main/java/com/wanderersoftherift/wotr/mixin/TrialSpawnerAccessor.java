package com.wanderersoftherift.wotr.mixin;

import net.minecraft.core.Holder;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawner;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawnerConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TrialSpawner.class)
public interface TrialSpawnerAccessor {

    @Accessor("normalConfig")
    void setNormalConfig(Holder<TrialSpawnerConfig> spawnerConfig);

    @Accessor("ominousConfig")
    void setOminousConfig(Holder<TrialSpawnerConfig> spawnerConfig);

    @Accessor("requiredPlayerRange")
    void setRequiredPlayerRange(int range);
}
