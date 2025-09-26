package com.wanderersoftherift.wotr.block.blockentity.anomaly;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.block.blockentity.AnomalyBlockEntity;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.serialization.LaxRegistryCodec;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.function.BiConsumer;

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

    int particleColor();

    AnomalyTaskDisplay taskDisplay(T task);

    default void handleMobDeath(LivingEntity mob, AnomalyBlockEntity anomalyBlockEntity, T state) {
    }

    /**
     *
     * @param serverLevel
     * @param anomalyBlockEntity
     * @param state
     * @return delay until next tick
     */
    default int scheduledTick(ServerLevel serverLevel, AnomalyBlockEntity anomalyBlockEntity, T state) {
        return -1;
    }

    record AnomalyTaskType<T>(MapCodec<? extends AnomalyTask<T>> mainCodec, Codec<T> stateCodec) {
    }

    interface AnomalyTaskDisplay {
        int getCount();

        void forEachIndexed(BiConsumer<Integer, ItemStack> func);
    }
}
