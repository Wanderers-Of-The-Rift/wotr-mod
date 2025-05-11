package com.wanderersoftherift.wotr.rift.objective.ongoing;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.rift.objective.LevelRiftObjectiveData;
import com.wanderersoftherift.wotr.rift.objective.OngoingObjective;
import com.wanderersoftherift.wotr.rift.objective.ProgressObjective;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

/**
 * Ongoing kill objective
 */
public class KillOngoingObjective implements ProgressObjective {

    public static final MapCodec<KillOngoingObjective> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Codec.INT.fieldOf("targetKills").forGetter(KillOngoingObjective::getTargetProgress),
                    Codec.INT.fieldOf("currentKills").forGetter(KillOngoingObjective::getCurrentProgress)
            ).apply(instance, KillOngoingObjective::new));

    private final int targetKills;
    private int currentKills;

    public KillOngoingObjective(int targetKills) {
        this(targetKills, 0);
    }

    public KillOngoingObjective(int targetKills, int currentKills) {
        this.targetKills = targetKills;
        this.currentKills = currentKills;
    }

    @Override
    public boolean onLivingDeath(LivingDeathEvent event, ServerLevel serverLevel, LevelRiftObjectiveData data) {
        if (isComplete()) {
            return false;
        }
        if (!MobCategory.MONSTER.equals(event.getEntity().getClassification(false))) {
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

    @Override
    public Component getObjectiveStartMessage() {
        return Component.translatable(WanderersOfTheRift.translationId("objective", "kill.description"), targetKills);
    }
}
