package com.wanderersoftherift.wotr.rift.anomaly.state;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.rift.anomaly.AnomalyTaskState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;

public record BattleTaskState(ResourceLocation trialSpawnerConfig, boolean isCleared, boolean isStarted)
        implements AnomalyTaskState {

    public static final MapCodec<BattleTaskState> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            ResourceLocation.CODEC.fieldOf("trial_spawner_config").forGetter(BattleTaskState::trialSpawnerConfig),
            Codec.BOOL.fieldOf("is_cleared").forGetter(BattleTaskState::isCleared),
            Codec.BOOL.fieldOf("is_started").forGetter(BattleTaskState::isStarted)
    ).apply(inst, BattleTaskState::new));

    public BattleTaskState(ResourceLocation trialSpawnerConfig, boolean isCleared) {
        this(trialSpawnerConfig, isCleared, false);
    }

    @Override
    public MapCodec<? extends AnomalyTaskState> getCodec() {
        return CODEC;
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand, ServerLevel level) {
        if (isCleared) {
            return InteractionResult.SUCCESS;
        }
        if (!isStarted) {
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public boolean isComplete() {
        return isCleared;
    }

    @Override
    public float[] getParticleColor() {
        return new float[] { 0.8f, 0.1f, 0.1f };
    }

    public BattleTaskState startBattle() {
        return new BattleTaskState(trialSpawnerConfig, false, true);
    }

    public BattleTaskState clearBattle() {
        return new BattleTaskState(trialSpawnerConfig, true, true);
    }
}
