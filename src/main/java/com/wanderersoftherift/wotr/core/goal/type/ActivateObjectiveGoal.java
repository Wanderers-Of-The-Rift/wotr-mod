package com.wanderersoftherift.wotr.core.goal.type;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.core.goal.Goal;
import com.wanderersoftherift.wotr.serialization.DualCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;

/**
 * A goal to activate objective blocks
 * 
 * @param count
 */
public record ActivateObjectiveGoal(int count) implements Goal {
    public static final MapCodec<ActivateObjectiveGoal> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ExtraCodecs.POSITIVE_INT.fieldOf("count").forGetter(ActivateObjectiveGoal::count)
    ).apply(instance, ActivateObjectiveGoal::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ActivateObjectiveGoal> STREAM_CODEC = StreamCodec
            .composite(
                    ByteBufCodecs.INT, ActivateObjectiveGoal::count, ActivateObjectiveGoal::new
            );

    public static final DualCodec<ActivateObjectiveGoal> TYPE = new DualCodec<>(CODEC, STREAM_CODEC);

    @Override
    public DualCodec<? extends Goal> getType() {
        return TYPE;
    }
}
