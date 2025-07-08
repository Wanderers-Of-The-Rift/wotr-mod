package com.wanderersoftherift.wotr.core.guild.quest;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.network.guild.QuestAcceptedPayload;
import com.wanderersoftherift.wotr.network.guild.QuestRemovedPayload;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.NbtOps;
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
import java.util.Optional;
import java.util.SequencedMap;

/**
 * Player attachment for holding the active quests of the player
 */
public final class ActiveQuests {

    private final IAttachmentHolder holder;
    @NotNull private final Data data;

    public ActiveQuests(@NotNull IAttachmentHolder holder) {
        this(holder, null);
    }

    ActiveQuests(@NotNull IAttachmentHolder holder, @Nullable Data data) {
        this.holder = holder;
        if (data != null) {
            this.data = data;
        } else {
            this.data = new Data();
        }
        for (QuestState quest : getQuestList()) {
            quest.setHolder(holder);
        }
    }

    public static IAttachmentSerializer<Tag, ActiveQuests> getSerializer() {
        return new IAttachmentSerializer<>() {
            @Override
            public @NotNull ActiveQuests read(
                    @NotNull IAttachmentHolder holder,
                    @NotNull Tag tag,
                    HolderLookup.@NotNull Provider provider) {
                return new ActiveQuests(holder,
                        Data.CODEC.decode(provider.createSerializationContext(NbtOps.INSTANCE), tag)
                                .getOrThrow()
                                .getFirst());
            }

            @Override
            public @Nullable Tag write(@NotNull ActiveQuests attachment, HolderLookup.@NotNull Provider provider) {
                return Data.CODEC.encodeStart(provider.createSerializationContext(NbtOps.INSTANCE), attachment.data)
                        .getOrThrow();
            }
        };
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
     * @param quest
     */
    public void remove(Holder<Quest> quest) {
        if (data.quests().remove(quest) != null) {
            if (holder instanceof ServerPlayer player) {
                PacketDistributor.sendToPlayer(player, new QuestRemovedPayload(quest));
            }
        }
    }

    /**
     * Adds a quest
     *
     * @param newQuest
     */
    public void add(Holder<Quest> newQuest) {
        add(new QuestState(newQuest));
    }

    /**
     * Adds a quest
     *
     * @param newQuest
     */
    public void add(QuestState newQuest) {
        data.quests().put(newQuest.getQuest(), newQuest);
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
    public void resyncFromServer(List<QuestState> newQuests) {
        data.quests().clear();
        for (QuestState newQuest : newQuests) {
            add(newQuest);
        }
    }

    public Optional<QuestState> getQuestState(Holder<Quest> quest) {
        return Optional.ofNullable(data.quests().get(quest));
    }

    public QuestState getQuestState(int slot) {
        return data.quests().values().stream().skip(slot).findFirst().orElse(null);
    }

    private record Data(SequencedMap<Holder<Quest>, QuestState> quests) {
        private static final Codec<ActiveQuests.Data> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.unboundedMap(Quest.CODEC, QuestState.CODEC)
                        .<SequencedMap<Holder<Quest>, QuestState>>xmap(LinkedHashMap::new, x -> x)
                        .fieldOf("quests")
                        .forGetter(ActiveQuests.Data::quests)
        ).apply(instance, ActiveQuests.Data::new));

        public Data() {
            this(new LinkedHashMap<>());
        }
    }
}
