package com.wanderersoftherift.wotr.rift.objective.ongoing;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.rift.objective.LevelRiftObjectiveData;
import com.wanderersoftherift.wotr.rift.objective.OngoingObjective;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

/**
 * Ongoing objective to be stealthy
 */
public class StealthOngoingObjective implements OngoingObjective {

    public static final MapCodec<StealthOngoingObjective> CODEC = RecordCodecBuilder.mapCodec(
            inst -> inst.group(
                    Codec.INT.fieldOf("alarm_progress").forGetter(StealthOngoingObjective::getAlarmProgress),
                    Codec.INT.fieldOf("target_progress").forGetter(StealthOngoingObjective::getTargetProgress)
            ).apply(inst, StealthOngoingObjective::new));

    private int alarmProgress;
    private final int targetProgress;

    public StealthOngoingObjective(int targetProgress) {
        this(0, targetProgress);
    }

    public StealthOngoingObjective(int alarmProgress, int targetProgress) {
        this.alarmProgress = alarmProgress;
        this.targetProgress = targetProgress;
    }

    public int getAlarmProgress() {
        return alarmProgress;
    }

    public int getTargetProgress() {
        return targetProgress;
    }

    public boolean isComplete() {
        return alarmProgress >= targetProgress;
    }

    @Override
    public boolean onLivingDeath(LivingDeathEvent event, ServerLevel serverLevel, LevelRiftObjectiveData data) {
        if (isComplete()) {
            return false;
        }
        alarmProgress += event.getEntity().tickCount;
        return true;
    }

    @Override
    public MapCodec<? extends OngoingObjective> getCodec() {
        return CODEC;
    }

    @Override
    public Component getObjectiveStartMessage() {
        return Component.translatable(WanderersOfTheRift.translationId("objective", "stealth.description"));
    }
}
