package com.wanderersoftherift.wotr.rift.anomaly;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.serialization.LaxRegistryCodec;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.LevelAccessor;

import java.util.function.Function;

public interface AnomalyTaskType {
    Codec<AnomalyTaskType> DIRECT_CODEC = WotrRegistries.ANOMALY_TASK_TYPES.byNameCodec()
            .dispatch(AnomalyTaskType::getCodec, Function.identity());
    Codec<Holder<AnomalyTaskType>> CODEC = LaxRegistryCodec.create(WotrRegistries.Keys.ANOMALY_TASKS);
    StreamCodec<RegistryFriendlyByteBuf, Holder<AnomalyTaskType>> STREAM_CODEC = ByteBufCodecs
            .holderRegistry(WotrRegistries.Keys.ANOMALY_TASKS);

    MapCodec<? extends AnomalyTaskType> getCodec();

    AnomalyTaskState generate(LevelAccessor level);

    int getWeight();
}
