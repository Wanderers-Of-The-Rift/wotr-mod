package com.wanderersoftherift.wotr.core.rift.objective.ongoing;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.core.goal.Goal;
import com.wanderersoftherift.wotr.core.goal.GoalState;
import com.wanderersoftherift.wotr.core.rift.RiftData;
import com.wanderersoftherift.wotr.core.rift.objective.OngoingObjective;
import com.wanderersoftherift.wotr.core.rift.parameter.RiftParameterData;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

public class GoalBasedOngoingObjective implements OngoingObjective {
    public static final MapCodec<GoalBasedOngoingObjective> CODEC = RecordCodecBuilder
            .mapCodec(instance -> instance.group(
                    Goal.DIRECT_CODEC.listOf().fieldOf("goals").forGetter(GoalBasedOngoingObjective::getGoals),
                    Codec.INT.listOf().fieldOf("goal_progress").forGetter(GoalBasedOngoingObjective::getGoalProgress)
            ).apply(instance, GoalBasedOngoingObjective::new));

    private final List<Goal> goals;
    private final int[] goalProgress;
    private final Multimap<Class<? extends Goal>, ObjectiveGoalState> goalLookup = ArrayListMultimap.create();

    public GoalBasedOngoingObjective(List<Goal> goals) {
        this(goals, List.of());
    }

    public GoalBasedOngoingObjective(List<Goal> goals, List<Integer> goalProgress) {
        this.goals = new ArrayList<>(goals);
        this.goalProgress = new int[goals.size()];
        for (int i = 0; i < goals.size() && i < goalProgress.size(); i++) {
            this.goalProgress[i] = goalProgress.get(i);
        }
        for (int i = 0; i < goals.size(); i++) {
            goalLookup.put(goals.get(i).getClass(), new ObjectiveGoalState(this, i));
        }
    }

    public List<Goal> getGoals() {
        return Collections.unmodifiableList(goals);
    }

    public IntList getGoalProgress() {
        return IntList.of(goalProgress);
    }

    public List<? extends GoalState> getGoalStates() {
        return IntStream.range(0, goals.size()).mapToObj(index -> new ObjectiveGoalState(this, index)).toList();
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
        // TODO: Need to make a setup to generate status component for goals?
        return Component.empty();
    }

    @Override
    public void registerUpdaters(RiftParameterData data, RiftData riftData, ServerLevel serverLevel) {

    }

    public <T extends Goal> boolean progressGoals(Class<T> type, Function<T, Integer> amount) {
        for (var goalInstance : goalLookup.get(type)) {
            int progress = goalInstance.getProgress();
            T goal = type.cast(goalInstance.getGoal());
            if (progress < goal.count()) {
                progress = Math.clamp(progress + amount.apply(goal), 0, goal.count());
                goalInstance.setProgress(progress);
                return true;
            }
        }
        return false;
    }

    private record ObjectiveGoalState(GoalBasedOngoingObjective objective, int index) implements GoalState {

        @Override
        public Goal getGoal() {
            return objective.getGoals().get(index);
        }

        @Override
        public int getProgress() {
            return objective.getGoalProgress().getInt(index);
        }

        public void setProgress(int amount) {
            objective.goalProgress[index] = amount;
        }
    }
}
