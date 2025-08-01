package com.wanderersoftherift.wotr.core.quest;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.network.quest.QuestAcceptedPayload;
import com.wanderersoftherift.wotr.network.quest.QuestRemovedPayload;
import com.wanderersoftherift.wotr.serialization.AttachmentSerializerFromDataCodec;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.SequencedMap;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Player attachment for holding the active quests of the player
 */
public final class ActiveQuests {

    private final @NotNull IAttachmentHolder holder;
    private final @NotNull Data data;

    private final Multimap<Class<? extends Goal>, GoalInstance> goalLookup = ArrayListMultimap.create();

    public ActiveQuests(@NotNull IAttachmentHolder holder) {
        this(holder, null);
    }

    private ActiveQuests(@NotNull IAttachmentHolder holder, @Nullable Data data) {
        this.holder = holder;
        this.data = Objects.requireNonNullElseGet(data, Data::new);
        for (QuestState quest : getQuestList()) {
            quest.setHolder(holder);
            registerGoals(quest);
        }
    }

    public static IAttachmentSerializer<Tag, ActiveQuests> getSerializer() {
        return new AttachmentSerializerFromDataCodec<>(Data.CODEC, ActiveQuests::new, x -> x.data);
    }

    public <T extends Goal> void progressGoals(Class<T> type, Function<T, Integer> amount) {
        for (var goalInstance : goalLookup.get(type)) {
            int progress = goalInstance.quest.getGoalProgress(goalInstance.index);
            T goal = type.cast(goalInstance.quest.getGoal(goalInstance.index));
            if (progress < goal.count()) {
                progress = Math.clamp(progress + amount.apply(goal), 0, goal.count());
                goalInstance.quest.setGoalProgress(goalInstance.index, progress);
            }
        }
    }

    /**
     * @return The active quests
     */
    public List<QuestState> getQuestList() {
        return new ArrayList<>(data.quests().values());
    }

    /**
     * @return The number of active quests
     */
    public int count() {
        return data.quests().size();
    }

    public boolean isEmpty() {
        return data.quests().isEmpty();
    }

    /**
     * Removes a quest from the active quests
     *
     * @param questId
     * @return whether a quest was removed
     */
    public boolean remove(UUID questId) {
        QuestState removedQuest = data.quests().remove(questId);
        if (removedQuest != null) {
            unregisterGoals(removedQuest);
            if (holder instanceof ServerPlayer player) {
                PacketDistributor.sendToPlayer(player, new QuestRemovedPayload(questId));
            }
            return true;
        }
        return false;
    }

    /**
     * Adds a quest
     *
     * @param newQuest
     */
    public void add(QuestState newQuest) {
        data.quests().put(newQuest.getId(), newQuest);
        newQuest.setHolder(holder);
        registerGoals(newQuest);
        if (holder instanceof ServerPlayer player) {
            PacketDistributor.sendToPlayer(player, new QuestAcceptedPayload(newQuest));
        }
    }

    /**
     * Updates the active quests from data replicated from the server
     *
     * @param newQuests
     */
    public void replaceAll(List<QuestState> newQuests) {
        data.quests().clear();
        goalLookup.clear();
        for (QuestState newQuest : newQuests) {
            add(newQuest);
        }
    }

    public Optional<QuestState> getQuestState(UUID quest) {
        return Optional.ofNullable(data.quests().get(quest));
    }

    private void registerGoals(QuestState quest) {
        for (int i = 0; i < quest.goalCount(); i++) {
            goalLookup.put(quest.getGoal(i).getClass(), new GoalInstance(quest, i));
        }
    }

    private void unregisterGoals(QuestState quest) {
        for (int i = 0; i < quest.goalCount(); i++) {
            goalLookup.remove(quest.getGoal(i).getClass(), new GoalInstance(quest, i));
        }
    }

    private record Data(SequencedMap<UUID, QuestState> quests) {
        private static final Codec<ActiveQuests.Data> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                QuestState.CODEC.listOf()
                        .<SequencedMap<UUID, QuestState>>xmap(
                                list -> list.stream()
                                        .collect(Collectors.toMap(QuestState::getId, x -> x, (a, b) -> a,
                                                LinkedHashMap::new)),
                                map -> List.copyOf(map.values())
                        )
                        .fieldOf("quests")
                        .forGetter(ActiveQuests.Data::quests)
        ).apply(instance, ActiveQuests.Data::new));

        public Data() {
            this(new LinkedHashMap<>());
        }
    }

    private record GoalInstance(QuestState quest, int index) {
    }
}
