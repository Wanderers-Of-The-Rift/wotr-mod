package com.wanderersoftherift.wotr.core.rift.objective;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.core.goal.Goal;
import com.wanderersoftherift.wotr.core.goal.GoalState;
import com.wanderersoftherift.wotr.core.goal.GoalTracker;
import com.wanderersoftherift.wotr.core.rift.objective.ongoing.GoalBasedOngoingObjective;
import com.wanderersoftherift.wotr.serialization.AttachmentSerializerFromDataCodec;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Attachment for level-wide objectives
 */
public class ObjectiveData implements GoalTracker {
    private final @NotNull ServerLevel holder;

    private OngoingObjective objective;

    public ObjectiveData(IAttachmentHolder holder) {
        this(holder, null);
    }

    private ObjectiveData(IAttachmentHolder holder, Data data) {
        if (!(holder instanceof ServerLevel level)) {
            throw new IllegalArgumentException("Holder must be a server level");
        }
        this.holder = level;
        if (data != null) {
            this.objective = data.objective.orElse(null);
        }
        if (objective != null) {
            objective.setLevel(level);
        }
    }

    public void setObjective(OngoingObjective objective) {
        this.objective = objective;
        if (objective != null) {
            objective.setLevel(holder);
        }
    }

    public Optional<OngoingObjective> getObjective() {
        return Optional.ofNullable(objective);
    }

    @Override
    public <T extends Goal> Stream<GoalState<T>> streamGoals(Class<T> goalType) {
        if (getObjective().orElse(null) instanceof GoalBasedOngoingObjective goalBasedOngoingObjective) {
            return goalBasedOngoingObjective.streamGoals(goalType);
        }
        return Stream.empty();
    }

    public static IAttachmentSerializer<Tag, ObjectiveData> getSerializer() {
        return new AttachmentSerializerFromDataCodec<>(Data.CODEC, ObjectiveData::new,
                objectiveData -> new Data(objectiveData.getObjective()));
    }

    private record Data(Optional<OngoingObjective> objective) {
        public static final Codec<Data> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                OngoingObjective.DIRECT_CODEC.optionalFieldOf("objective").forGetter(Data::objective)
        ).apply(instance, Data::new));
    }
}
