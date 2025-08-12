package com.wanderersoftherift.wotr.core.quest.goal;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.core.quest.Goal;
import com.wanderersoftherift.wotr.core.rift.predicate.RiftPredicate;
import com.wanderersoftherift.wotr.serialization.DualCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;

/**
 * A goal to complete some number of rifts, with optional conditions
 * 
 * @param count           The number of rifts to complete
 * @param completionLevel Minimum level at which the rift must be completed
 * @param predicate       Any conditions on whether a rift will count for completion
 */
public record CompleteRiftGoal(int count, RiftCompletionLevel completionLevel, RiftPredicate predicate)
        implements Goal {

    public static final MapCodec<CompleteRiftGoal> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.optionalFieldOf("count", 1).forGetter(CompleteRiftGoal::count),
            RiftCompletionLevel.CODEC.optionalFieldOf("completion_level", RiftCompletionLevel.COMPLETE)
                    .forGetter(CompleteRiftGoal::completionLevel),
            RiftPredicate.CODEC
                    .optionalFieldOf("rift_type",
                            new RiftPredicate(Optional.empty(), Optional.empty(), Optional.empty()))
                    .forGetter(CompleteRiftGoal::predicate)
    ).apply(instance, CompleteRiftGoal::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CompleteRiftGoal> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, CompleteRiftGoal::count, RiftCompletionLevel.STREAM_CODEC,
            CompleteRiftGoal::completionLevel, RiftPredicate.STREAM_CODEC, CompleteRiftGoal::predicate,
            CompleteRiftGoal::new
    );

    public static final DualCodec<CompleteRiftGoal> TYPE = new DualCodec<>(CODEC, STREAM_CODEC);

    @Override
    public DualCodec<CompleteRiftGoal> getType() {
        return TYPE;
    }

}
