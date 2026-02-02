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
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
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
                    Codec.unboundedMap(UUIDUtil.STRING_CODEC,
                            Codec.INT.listOf().<IntList>xmap(IntArrayList::new, x -> x))
                            .fieldOf("per_player_progress")
                            .forGetter(x -> x.perPlayerGoalProgress)
            ).apply(instance, GoalBasedOngoingObjective::new));

    private final List<Goal> goals;
    private final List<Reward> rewards;
    private final int[] goalProgress;
    private final Map<UUID, IntList> perPlayerGoalProgress;
    private final ListMultimap<Class<? extends Goal>, ObjectiveGoalState<?>> goalLookup = ArrayListMultimap.create();
    private ServerLevel level;

    public GoalBasedOngoingObjective(List<Goal> goals, List<Reward> rewards) {
        this(goals, rewards, Map.of());
    }

    public GoalBasedOngoingObjective(List<Goal> goals, List<Reward> rewards, Map<UUID, IntList> perPlayerGoalProgress) {
        this.goals = new ArrayList<>(goals);
        this.rewards = new ArrayList<>(rewards);
        this.goalProgress = new int[goals.size()];
        this.perPlayerGoalProgress = new LinkedHashMap<>();
        for (var entry : perPlayerGoalProgress.entrySet()) {
            IntList values = new IntArrayList(entry.getValue());
            while (values.size() < goals.size()) {
                values.add(0);
            }
            this.perPlayerGoalProgress.put(entry.getKey(), values);
        }
        this.perPlayerGoalProgress.values().forEach(values -> {
            for (int i = 0; i < goals.size(); i++) {
                goalProgress[i] += values.getInt(i);
            }
        });
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

    private int sumTotalProgress(int index) {
        return perPlayerGoalProgress.values().stream().mapToInt(x -> x.getInt(index)).sum();
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

        public void setProgress(Player player, int amount) {

            IntList playerProgress = objective.perPlayerGoalProgress.computeIfAbsent(player.getUUID(),
                    (uuid) -> new IntArrayList(new int[objective.goals.size()])
            );
            if (playerProgress.set(index, amount) == amount) {
                return;
            }
            int clampedAmount = Math.clamp(objective.sumTotalProgress(index), 0, getGoal().count());
            if (clampedAmount != objective.goalProgress[index]) {
                objective.goalProgress[index] = clampedAmount;
                PacketDistributor.sendToPlayersInDimension(objective.level,
                        new S2CRiftObjectiveStatusPacket(Optional.of(objective)));
            }
        }
    }
}
