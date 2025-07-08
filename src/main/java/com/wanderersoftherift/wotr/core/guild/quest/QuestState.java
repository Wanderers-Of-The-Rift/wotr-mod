package com.wanderersoftherift.wotr.core.guild.quest;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.network.guild.QuestGoalUpdatePayload;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

/**
 * An active quest tracks the progress of a quest that a player has accepted.
 * <p>
 * Progress is tracked as an integer for each goal, with each goal providing a target value
 * </p>
 */
public class QuestState {

    public static final Codec<QuestState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Quest.CODEC.fieldOf("quest").forGetter(QuestState::getQuest),
            Codec.INT.listOf().fieldOf("goal_states").forGetter(x -> IntArrayList.wrap(x.goalProgress))
    ).apply(instance, QuestState::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, QuestState> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.holderRegistry(WotrRegistries.Keys.QUESTS), QuestState::getQuest,
            ByteBufCodecs.INT.apply(ByteBufCodecs.list()), x -> IntArrayList.wrap(x.goalProgress), QuestState::new
    );

    private IAttachmentHolder holder;
    private Holder<Quest> quest;
    private int[] goalProgress;

    public QuestState(Holder<Quest> quest) {
        this.quest = quest;
        this.goalProgress = new int[quest.value().goals().size()];
    }

    public QuestState(Holder<Quest> quest, List<Integer> states) {
        this.quest = quest;
        this.goalProgress = new int[quest.value().goals().size()];
        for (int i = 0; i < goalProgress.length && i < states.size(); i++) {
            goalProgress[i] = states.get(i);
        }
    }

    public void setHolder(IAttachmentHolder holder) {
        this.holder = holder;
        if (holder instanceof ServerPlayer player) {
            for (int i = 0; i < goalCount(); i++) {
                quest.value().goals().get(i).registerActiveQuest(player, this, i);
            }
        }
    }

    /**
     * @return The definition of the quest that has been accepted
     */
    public Holder<Quest> getQuest() {
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
        if (holder instanceof ServerPlayer player) {
            PacketDistributor.sendToPlayer(player, new QuestGoalUpdatePayload(quest, index, amount));
        }
    }
}
