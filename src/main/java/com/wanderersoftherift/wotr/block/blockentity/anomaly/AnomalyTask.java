package com.wanderersoftherift.wotr.block.blockentity.anomaly;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.block.blockentity.AnomalyBlockEntity;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.serialization.LaxRegistryCodec;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;

public interface AnomalyTask<T> {

    Codec<AnomalyTask<?>> DIRECT_CODEC = WotrRegistries.ANOMALY_TASK_TYPE.byNameCodec()
            .dispatch(AnomalyTask::type, AnomalyTaskType::mainCodec);
    Codec<Holder<AnomalyTask<?>>> HOLDER_CODEC = LaxRegistryCodec.refOrDirect(WotrRegistries.Keys.ANOMALY_TASK,
            DIRECT_CODEC);

    static <T> Codec<Holder<AnomalyTask<T>>> holderCodec() {
        return (Codec<Holder<AnomalyTask<T>>>) (Object) HOLDER_CODEC;
    }

    InteractionResult interact(Player player, InteractionHand hand, AnomalyBlockEntity anomalyBlockEntity, T state);

    AnomalyTaskType<T> type();

    T createState(RandomSource rng);

    record AnomalyTaskType<T>(MapCodec<? extends AnomalyTask<T>> mainCodec, Codec<T> stateCodec) {
    }
}
