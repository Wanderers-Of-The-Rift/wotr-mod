package com.wanderersoftherift.wotr.core.guild.quest;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

/**
 * An active quest tracks the progress of a quest that a player has accepted.
 * <p>
 * Progress is tracked as an integer for each goal, with each goal providing a target value
 * </p>
 */
public class ActiveQuest {

    public static final Codec<ActiveQuest> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Quest.CODEC.fieldOf("quest").forGetter(ActiveQuest::getBaseQuest),
            Codec.INT.listOf().fieldOf("goal_states").forGetter(x -> IntArrayList.wrap(x.goalProgress))
    ).apply(instance, ActiveQuest::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ActiveQuest> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.holderRegistry(WotrRegistries.Keys.QUESTS), ActiveQuest::getBaseQuest,
            ByteBufCodecs.INT.apply(ByteBufCodecs.list()), x -> IntArrayList.wrap(x.goalProgress), ActiveQuest::new
    );

    private Holder<Quest> quest;
    private int[] goalProgress;

    public ActiveQuest(Holder<Quest> quest) {
        this.quest = quest;
        this.goalProgress = new int[quest.value().goals().size()];
    }

    public ActiveQuest(Holder<Quest> quest, List<Integer> states) {
        this.quest = quest;
        this.goalProgress = new int[quest.value().goals().size()];
        for (int i = 0; i < goalProgress.length && i < states.size(); i++) {
            goalProgress[i] = states.get(i);
        }
    }

    /**
     * @return The definition of the quest that has been accepted
     */
    public Holder<Quest> getBaseQuest() {
        return quest;
    }

    /**
     * @return The number of goals that compose the quest
     */
    public int goalCount() {
        return quest.value().goals().size();
    }

    /**
     * @param index
     * @return The nth goal of the quest
     */
    public Goal getGoal(int index) {
        Preconditions.checkArgument(index >= 0 && index < quest.value().goals().size(), "Index out of bounds");
        return quest.value().goals().get(index);
    }

    /**
     * @return Whether th quest is complete
     */
    public boolean isComplete() {
        for (int i = 0; i < quest.value().goals().size(); i++) {
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
        return goalProgress[index] >= quest.value().goals().get(index).progressTarget();
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
    }

    /**
     * Updates this active quest from information replicated from the server
     * 
     * @param activeQuest
     */
    public void updateFromServer(ActiveQuest activeQuest) {
        this.quest = activeQuest.getBaseQuest();
        this.goalProgress = activeQuest.goalProgress;
    }
}
