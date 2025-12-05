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
 * A goal to enter rift rooms
 * 
 * @param count
 */
public record VisitRoomGoal(int count) implements Goal {
    public static final MapCodec<VisitRoomGoal> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ExtraCodecs.POSITIVE_INT.fieldOf("count").forGetter(VisitRoomGoal::count)
    ).apply(instance, VisitRoomGoal::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, VisitRoomGoal> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, VisitRoomGoal::count, VisitRoomGoal::new
    );

    public static final DualCodec<VisitRoomGoal> TYPE = new DualCodec<>(CODEC, STREAM_CODEC);

    @Override
    public DualCodec<? extends Goal> getType() {
        return TYPE;
    }
}
