package com.wanderersoftherift.wotr.rift.objective.ongoing;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.rift.RiftData;
import com.wanderersoftherift.wotr.core.rift.parameter.RiftParameterData;
import com.wanderersoftherift.wotr.core.rift.parameter.definitions.RiftParameter;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.network.rift.S2CRiftObjectiveStatusPacket;
import com.wanderersoftherift.wotr.rift.objective.OngoingObjective;
import com.wanderersoftherift.wotr.rift.objective.ProgressObjective;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Optional;

/**
 * Ongoing kill objective
 */
public class KillOngoingObjective implements ProgressObjective {

    public static final MapCodec<KillOngoingObjective> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    ResourceKey.codec(WotrRegistries.Keys.RIFT_PARAMETER_CONFIGS)
                            .fieldOf("target_parameter_key")
                            .forGetter(KillOngoingObjective::getTargetParameterKey),
                    Codec.INT.fieldOf("target_kills")
                            .forGetter(killOngoingObjective -> killOngoingObjective.getTargetProgress()),
                    Codec.INT.fieldOf("current_kills").forGetter(KillOngoingObjective::getCurrentProgress)
            ).apply(instance, KillOngoingObjective::new));

    private final ResourceKey<RiftParameter> targetParameterKey;
    private int targetKills;
    private int currentKills;

    public KillOngoingObjective(ResourceKey<RiftParameter> targetParameterKey, int targetKills) {
        this(targetParameterKey, targetKills, 0);
    }

    public KillOngoingObjective(ResourceKey<RiftParameter> targetParameterKey, int targetKills, int currentKills) {
        this.targetParameterKey = targetParameterKey;
        this.targetKills = targetKills;
        this.currentKills = currentKills;
    }

    @Override
    public boolean onLivingDeath(LivingDeathEvent event, ServerLevel serverLevel, RiftData data) {
        if (isComplete()) {
            return false;
        }
        if (!MobCategory.MONSTER.equals(event.getEntity().getClassification(false))) {
            return false;
        }
        if (!(event.getSource().getEntity() instanceof Player)) {
            return false;
        }
        currentKills++;
        return true;
    }

    @Override
    public MapCodec<? extends OngoingObjective> getCodec() {
        return CODEC;
    }

    @Override
    public int getCurrentProgress() {
        return currentKills;
    }

    @Override
    public int getTargetProgress() {
        return targetKills;
    }

    public void setTargetProgress(int target) {
        this.targetKills = target;
    }

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

    private ResourceKey<RiftParameter> getTargetParameterKey() {
        return targetParameterKey;
    }

    @Override
    public Component getObjectiveStartMessage() {
        return Component.translatable(WanderersOfTheRift.translationId("objective", "kill.description"), targetKills);
    }
}
