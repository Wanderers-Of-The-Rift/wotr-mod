package com.wanderersoftherift.wotr.rift.anomaly;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;

import java.util.function.Function;

import static com.wanderersoftherift.wotr.init.WotrRegistries.ANOMALY_TASK_STATE_TYPES;

public interface AnomalyTaskState {
    Codec<AnomalyTaskState> DIRECT_CODEC = ANOMALY_TASK_STATE_TYPES.byNameCodec()
            .dispatch(AnomalyTaskState::getCodec, Function.identity());

    MapCodec<? extends AnomalyTaskState> getCodec();

    InteractionResult interact(Player player, InteractionHand hand, ServerLevel level);

    boolean isComplete();

    float[] getParticleColor();
}
