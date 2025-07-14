package com.wanderersoftherift.wotr.core.guild.quest.goal;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.core.guild.quest.Goal;
import com.wanderersoftherift.wotr.core.guild.quest.GoalType;
import com.wanderersoftherift.wotr.core.guild.quest.QuestEventHandler;
import com.wanderersoftherift.wotr.core.guild.quest.QuestState;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

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

    public static final GoalType<CompleteRiftGoal> TYPE = new GoalType<>(CODEC, STREAM_CODEC);

    @Override
    public GoalType<?> getType() {
        return TYPE;
    }

    @Override
    public int progressTarget() {
        return count;
    }

    @Override
    public void register(ServerPlayer player, QuestState quest, int goalIndex) {
        QuestEventHandler.registerRiftCompletionListener(player, quest, goalIndex);
    }
}
