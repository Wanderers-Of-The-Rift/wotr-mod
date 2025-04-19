package com.wanderersoftherift.wotr.rift.objective.types;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.rift.objective.AbstractObjective;
import com.wanderersoftherift.wotr.rift.objective.LevelRiftObjectiveData;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

public class StealthObjective extends AbstractObjective {

    public static MapCodec<StealthObjective> CODEC = RecordCodecBuilder.mapCodec(inst -> inst
            .group(Codec.INT.fieldOf("alarm_progress").forGetter(StealthObjective::getAlarmProgress),
                    Codec.BOOL.fieldOf("is_complete").forGetter(StealthObjective::isComplete))
            .apply(inst, StealthObjective::new));

    private int alarmProgress;
    private final int maxProgress = 20_000;
    private boolean complete;

    public StealthObjective() {
        this(0, false);
    }

    public StealthObjective(int alarmProgress, boolean complete) {
        this.alarmProgress = alarmProgress;
        this.complete = complete;
    }

    public int getAlarmProgress() {
        return alarmProgress;
    }

    public int getMaxProgress() {
        return maxProgress;
    }

    public boolean isComplete() {
        return complete;
    }

    @Override
    public boolean onLivingDeath(LivingDeathEvent event, ServerLevel serverLevel, LevelRiftObjectiveData data) {
        if (complete) return false;
        alarmProgress += event.getEntity().tickCount;
        if (alarmProgress >= maxProgress) {
            complete = true;
        }
        return true;
    }

    @Override
    public MapCodec<? extends AbstractObjective> getCodec() {
        return CODEC;
    }
}
