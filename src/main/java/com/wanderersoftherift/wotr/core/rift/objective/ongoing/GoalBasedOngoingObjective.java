package com.wanderersoftherift.wotr.core.rift.objective.ongoing;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.core.goal.Goal;
import com.wanderersoftherift.wotr.core.goal.GoalState;
import com.wanderersoftherift.wotr.core.goal.GoalTracker;
import com.wanderersoftherift.wotr.core.quest.Reward;
import com.wanderersoftherift.wotr.core.rift.RiftData;
import com.wanderersoftherift.wotr.core.rift.objective.OngoingObjective;
import com.wanderersoftherift.wotr.core.rift.parameter.RiftParameterData;
import com.wanderersoftherift.wotr.network.rift.S2CRiftObjectiveStatusPacket;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * An objective requiring completion of a number of goals.
 */
public class GoalBasedOngoingObjective implements OngoingObjective, GoalTracker {
    public static final MapCodec<GoalBasedOngoingObjective> CODEC = RecordCodecBuilder
            .mapCodec(instance -> instance.group(
                    Goal.DIRECT_CODEC.listOf().fieldOf("goals").forGetter(GoalBasedOngoingObjective::getGoals),
                    Reward.DIRECT_CODEC.listOf().fieldOf("rewards").forGetter(GoalBasedOngoingObjective::getRewards),
                    Codec.INT.listOf().fieldOf("goal_progress").forGetter(GoalBasedOngoingObjective::getGoalProgress)
            ).apply(instance, GoalBasedOngoingObjective::new));

    private final List<Goal> goals;
    private final List<Reward> rewards;
    private final int[] goalProgress;
    private final ListMultimap<Class<? extends Goal>, ObjectiveGoalState<?>> goalLookup = ArrayListMultimap.create();
    private ServerLevel level;

    public GoalBasedOngoingObjective(List<Goal> goals, List<Reward> rewards) {
        this(goals, rewards, List.of());
    }

    public GoalBasedOngoingObjective(List<Goal> goals, List<Reward> rewards, List<Integer> goalProgress) {
        this.goals = new ArrayList<>(goals);
        this.rewards = new ArrayList<>(rewards);
        this.goalProgress = new int[goals.size()];
        for (int i = 0; i < goals.size() && i < goalProgress.size(); i++) {
            this.goalProgress[i] = goalProgress.get(i);
        }
        for (int i = 0; i < goals.size(); i++) {
            goalLookup.put(goals.get(i).getClass(), new ObjectiveGoalState<>(this, i));
        }
    }

    public List<Goal> getGoals() {
        return Collections.unmodifiableList(goals);
    }

    @Override
    public List<Reward> getRewards() {
        return Collections.unmodifiableList(rewards);
    }

    public IntList getGoalProgress() {
        return IntList.of(goalProgress);
    }

    public List<? extends GoalState<?>> getGoalStates() {
        return IntStream.range(0, goals.size()).mapToObj(index -> new ObjectiveGoalState<>(this, index)).toList();
    }

    @Override
    public void setLevel(ServerLevel level) {
        this.level = level;
    }

    @Override
    public MapCodec<? extends OngoingObjective> getCodec() {
        return CODEC;
    }

    @Override
    public boolean isComplete() {
        for (int i = 0; i < goals.size(); i++) {
            if (goalProgress[i] < goals.get(i).count()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Component getObjectiveStartMessage() {
        return Component.empty();
    }

    @Override
    public void registerUpdaters(RiftParameterData data, RiftData riftData, ServerLevel serverLevel) {

    }

    @Override
    public <T extends Goal> Stream<GoalState<T>> streamGoals(Class<T> goalType) {
        // noinspection unchecked
        return goalLookup.get(goalType).stream().map(state -> (GoalState<T>) state);
    }

    private record ObjectiveGoalState<T extends Goal>(GoalBasedOngoingObjective objective, int index)
            implements GoalState<T> {

        @SuppressWarnings("unchecked")
        @Override
        public T getGoal() {
            return (T) objective.getGoals().get(index);
        }

        @Override
        public int getProgress() {
            return objective.getGoalProgress().getInt(index);
        }

        public void setProgress(int amount) {
            int clampedAmount = Math.clamp(amount, 0, getGoal().count());
            if (clampedAmount != objective.goalProgress[index]) {
                objective.goalProgress[index] = clampedAmount;
                PacketDistributor.sendToPlayersInDimension(objective.level,
                        new S2CRiftObjectiveStatusPacket(Optional.of(objective)));
            }
        }
    }
}
