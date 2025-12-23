package com.wanderersoftherift.wotr.core.goal.type;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.block.blockentity.anomaly.AnomalyTask;
import com.wanderersoftherift.wotr.core.goal.Goal;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.serialization.DualCodec;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;

import java.util.Optional;

/**
 * A goal to close a number of anomalies, optionally of a required type
 * 
 * @param count
 * @param anomalyType
 */
public record CloseAnomalyGoal(int count, Optional<Holder<AnomalyTask.AnomalyTaskType<?>>> anomalyType)
        implements Goal {
    public static final MapCodec<CloseAnomalyGoal> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ExtraCodecs.POSITIVE_INT.fieldOf("count").forGetter(CloseAnomalyGoal::count),
            WotrRegistries.ANOMALY_TASK_TYPE.holderByNameCodec()
                    .optionalFieldOf("anomaly_type")
                    .forGetter(CloseAnomalyGoal::anomalyType)
    ).apply(instance, CloseAnomalyGoal::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CloseAnomalyGoal> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, CloseAnomalyGoal::count,
            ByteBufCodecs.optional(ByteBufCodecs.holderRegistry(WotrRegistries.Keys.ANOMALY_TASK_TYPE)),
            CloseAnomalyGoal::anomalyType, CloseAnomalyGoal::new
    );

    public static final DualCodec<CloseAnomalyGoal> TYPE = new DualCodec<>(CODEC, STREAM_CODEC);

    @Override
    public DualCodec<? extends Goal> getType() {
        return TYPE;
    }
}
