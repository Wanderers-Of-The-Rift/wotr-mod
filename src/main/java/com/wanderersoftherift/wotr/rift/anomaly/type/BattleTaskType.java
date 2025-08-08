package com.wanderersoftherift.wotr.rift.anomaly.type;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.rift.anomaly.AnomalyTaskState;
import com.wanderersoftherift.wotr.rift.anomaly.AnomalyTaskType;
import com.wanderersoftherift.wotr.rift.anomaly.state.BattleTaskState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.LevelAccessor;

public record BattleTaskType(int weight, float battleScale, ResourceLocation trialSpawnerConfig)
        implements AnomalyTaskType {

    public static final MapCodec<BattleTaskType> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            ExtraCodecs.POSITIVE_INT.fieldOf("weight").forGetter(BattleTaskType::weight),
            ExtraCodecs.POSITIVE_FLOAT.fieldOf("battle_scale").forGetter(BattleTaskType::battleScale),
            ResourceLocation.CODEC.fieldOf("trial_spawner_config").forGetter(BattleTaskType::trialSpawnerConfig)
    ).apply(inst, BattleTaskType::new));

    @Override
    public MapCodec<? extends AnomalyTaskType> getCodec() {
        return CODEC;
    }

    @Override
    public AnomalyTaskState generate(LevelAccessor level) {
        return new BattleTaskState(trialSpawnerConfig, false);
    }

    @Override
    public int getWeight() {
        return weight;
    }

}
