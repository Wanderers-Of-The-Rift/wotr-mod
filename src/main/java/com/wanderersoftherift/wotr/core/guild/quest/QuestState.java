package com.wanderersoftherift.wotr.core.guild.quest;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.network.guild.QuestGoalUpdatePayload;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.core.Holder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * The state if an active quest that a player has accepted.
 * <p>
 * Progress is tracked as an integer for each goal, with each goal providing a target value
 * </p>
 */
public class QuestState {

    public static final Codec<QuestState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            UUIDUtil.CODEC.fieldOf("id").forGetter(QuestState::getId),
            Quest.CODEC.fieldOf("origin").forGetter(QuestState::getOrigin),
            Goal.DIRECT_CODEC.listOf().fieldOf("goals").forGetter(QuestState::getGoals),
            Reward.DIRECT_CODEC.listOf().fieldOf("rewards").forGetter(QuestState::getRewards),
            Codec.INT.listOf().fieldOf("goal_states").forGetter(x -> IntArrayList.wrap(x.goalProgress))
    ).apply(instance, QuestState::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, QuestState> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, QuestState::getId, ByteBufCodecs.holderRegistry(WotrRegistries.Keys.QUESTS),
            QuestState::getOrigin, Goal.STREAM_CODEC.apply(ByteBufCodecs.list()), QuestState::getGoals,
            Reward.STREAM_CODEC.apply(ByteBufCodecs.list()), QuestState::getRewards,
            ByteBufCodecs.INT.apply(ByteBufCodecs.list()), x -> IntArrayList.wrap(x.goalProgress), QuestState::new
    );

    private IAttachmentHolder holder;
    private final UUID id;
    private final Holder<Quest> origin;
    private final List<Goal> goals;
    private final List<Reward> rewards;
    private final int[] goalProgress;

    public QuestState(Holder<Quest> origin, Collection<Goal> goals, List<Reward> rewards) {
        this.id = UUID.randomUUID();
        this.origin = origin;
        this.goals = ImmutableList.copyOf(goals);
        this.rewards = ImmutableList.copyOf(rewards);
        this.goalProgress = new int[goals.size()];
    }

    public QuestState(UUID id, Holder<Quest> origin, List<Goal> goals, List<Reward> rewards, List<Integer> progress) {
        this.id = id;
        this.origin = origin;
        this.goals = goals;
        this.rewards = rewards;
        this.goalProgress = new int[goals.size()];
        for (int i = 0; i < goalProgress.length && i < progress.size(); i++) {
            goalProgress[i] = progress.get(i);
        }
    }

    public void setHolder(IAttachmentHolder holder) {
        this.holder = holder;
        if (holder instanceof ServerPlayer player) {
            for (int i = 0; i < goals.size(); i++) {
                goals.get(i).register(player, this, i);
            }
        }
    }

    public UUID getId() {
        return id;
    }

    public Holder<Quest> getOrigin() {
        return origin;
    }

    public List<Goal> getGoals() {
        return goals;
    }

    public List<Reward> getRewards() {
        return rewards;
    }

    /**
     * @return The number of goals that compose the quest
     */
    public int goalCount() {
        return goals.size();
    }

    /**
     * @param index
     * @return The nth goal of the quest
     */
    public Goal getGoal(int index) {
        Preconditions.checkArgument(index >= 0 && index < goalCount(), "Index out of bounds");
        return goals.get(index);
    }

    /**
     * @return Whether th quest is complete
     */
    public boolean isComplete() {
        for (int i = 0; i < goals.size(); i++) {
            if (!isGoalComplete(i)) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param index
     * @return The progress of the nth quest
     */
    public int getGoalProgress(int index) {
        Preconditions.checkArgument(index >= 0 && index < goalProgress.length, "Index out of bounds");
        return goalProgress[index];
    }

    /**
     * @param index
     * @return Whether the nth goal is complete
     */
    public boolean isGoalComplete(int index) {
        Preconditions.checkArgument(index >= 0 && index < goalProgress.length, "Index out of bounds");
        return goalProgress[index] >= getGoal(index).progressTarget();
    }

    /**
     * Sets the progress of the nth goal
     * 
     * @param index
     * @param amount
     */
    public void setGoalProgress(int index, int amount) {
        Preconditions.checkArgument(index >= 0 && index < goalProgress.length, "Index out of bounds");
        goalProgress[index] = amount;
        if (holder instanceof ServerPlayer player) {
            PacketDistributor.sendToPlayer(player, new QuestGoalUpdatePayload(id, index, amount));
        }
    }
}
