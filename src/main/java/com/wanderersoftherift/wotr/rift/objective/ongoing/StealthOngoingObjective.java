package com.wanderersoftherift.wotr.rift.objective.ongoing;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.rift.RiftData;
import com.wanderersoftherift.wotr.core.rift.parameter.RiftParameterData;
import com.wanderersoftherift.wotr.network.rift.S2CRiftObjectiveStatusPacket;
import com.wanderersoftherift.wotr.rift.objective.OngoingObjective;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Optional;

/**
 * Ongoing objective to be stealthy
 */
public class StealthOngoingObjective implements OngoingObjective {

    public static final MapCodec<StealthOngoingObjective> CODEC = RecordCodecBuilder.mapCodec(
            inst -> inst.group(
                    ResourceLocation.CODEC.fieldOf("target_parameter_key")
                            .forGetter(StealthOngoingObjective::getTargetParameterKey),
                    Codec.INT.fieldOf("target_progress").forGetter(StealthOngoingObjective::getTargetProgress),
                    Codec.INT.fieldOf("alarm_progress").forGetter(StealthOngoingObjective::getAlarmProgress)
            ).apply(inst, StealthOngoingObjective::new));

    private final ResourceLocation targetParameterKey;
    private int targetProgress;
    private int alarmProgress;

    public StealthOngoingObjective(ResourceLocation targetParameterKey, int targetProgress) {
        this(targetParameterKey, targetProgress, 0);
    }

    public StealthOngoingObjective(ResourceLocation targetParameterKey, int targetProgress, int alarmProgress) {
        this.targetParameterKey = targetParameterKey;
        this.targetProgress = targetProgress;
        this.alarmProgress = alarmProgress;
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
    public boolean onLivingDeath(LivingDeathEvent event, ServerLevel serverLevel, RiftData data) {
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

    public void setTargetProgress(int target) {
        this.targetProgress = target;
    }

    @Override
    public void registerUpdaters(RiftParameterData data, RiftData riftData, ServerLevel serverLevel) {
        var param = data.getParameter(targetParameterKey);
        if (param != null) {
            param.registerListener(newValue -> {
                setTargetProgress(newValue.intValue());
                riftData.setDirty();
                PacketDistributor.sendToPlayersInDimension(serverLevel,
                        new S2CRiftObjectiveStatusPacket(Optional.of(this)));
            });
        }
    }

    private ResourceLocation getTargetParameterKey() {
        return targetParameterKey;
    }

    @Override
    public Component getObjectiveStartMessage() {
        return Component.translatable(WanderersOfTheRift.translationId("objective", "stealth.description"));
    }
}
