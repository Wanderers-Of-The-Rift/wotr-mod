package com.wanderersoftherift.wotr.core.guild.quest;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.network.guild.QuestAcceptedPayload;
import com.wanderersoftherift.wotr.network.guild.QuestRemovedPayload;
import com.wanderersoftherift.wotr.serialization.AttachmentSerializerFromDataCodec;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.SequencedMap;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Player attachment for holding the active quests of the player
 */
public final class ActiveQuests {

    private final @NotNull IAttachmentHolder holder;
    private final @NotNull Data data;

    public ActiveQuests(@NotNull IAttachmentHolder holder) {
        this(holder, null);
    }

    private ActiveQuests(@NotNull IAttachmentHolder holder, @Nullable Data data) {
        this.holder = holder;
        this.data = Objects.requireNonNullElseGet(data, Data::new);
        for (QuestState quest : getQuestList()) {
            quest.setHolder(holder);
        }
    }

    public static IAttachmentSerializer<Tag, ActiveQuests> getSerializer() {
        return new AttachmentSerializerFromDataCodec<>(Data.CODEC, ActiveQuests::new, x -> x.data);
    }

    /**
     * @return The active quests
     */
    public Collection<QuestState> getQuestList() {
        return Collections.unmodifiableCollection(data.quests().values());
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
     */
    public void remove(UUID questId) {
        if (data.quests().remove(questId) != null) {
            if (holder instanceof ServerPlayer player) {
                PacketDistributor.sendToPlayer(player, new QuestRemovedPayload(questId));
            }
        }
    }

    /**
     * Adds a quest
     *
     * @param newQuest
     */
    public void add(QuestState newQuest) {
        data.quests().put(newQuest.getId(), newQuest);
        newQuest.setHolder(holder);
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
        for (QuestState newQuest : newQuests) {
            add(newQuest);
        }
    }

    public Optional<QuestState> getQuestState(UUID quest) {
        return Optional.ofNullable(data.quests().get(quest));
    }

    public QuestState getQuestState(int slot) {
        return data.quests().values().stream().skip(slot).findFirst().orElse(null);
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
}
