package com.wanderersoftherift.wotr.core.quest;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.serialization.GuavaCodecs;
import net.minecraft.core.UUIDUtil;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Tracking of quests available to the player
 */
// TODO: Remove support for the block? Or add full support for quests available from different sources (e.g. guild rather than npc)
public class AvailableQuests {
    public static final Codec<AvailableQuests> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            GuavaCodecs.unboundListMultimap(UUIDUtil.STRING_CODEC, QuestState.CODEC)
                    .fieldOf("available_npc_quests")
                    .forGetter(x -> x.availableNpcQuests),
            QuestState.CODEC.listOf().fieldOf("available_block_quests").forGetter(x -> x.availableBlockQuests)
    ).apply(instance, AvailableQuests::new));

    private final ListMultimap<UUID, QuestState> availableNpcQuests = ArrayListMultimap.create();
    private final List<QuestState> availableBlockQuests = new ArrayList<>();

    public AvailableQuests() {
    }

    private AvailableQuests(ListMultimap<UUID, QuestState> availableNpcQuests, List<QuestState> availableBlockQuests) {
        this.availableNpcQuests.putAll(availableNpcQuests);
        this.availableBlockQuests.addAll(availableBlockQuests);
    }

    public List<QuestState> getQuests(UUID npc) {
        if (npc == null) {
            return Collections.unmodifiableList(availableBlockQuests);
        }
        return Collections.unmodifiableList(availableNpcQuests.get(npc));
    }

    public void setQuests(@Nullable UUID npc, List<QuestState> availableQuests) {
        if (npc == null) {
            availableBlockQuests.clear();
            availableBlockQuests.addAll(availableQuests);
        } else {
            availableNpcQuests.removeAll(npc);
            availableNpcQuests.putAll(npc, availableQuests);
        }
    }

    public void removeQuest(QuestState state) {
        if (state.getQuestGiver() == null) {
            availableBlockQuests.remove(state);
        } else {
            availableNpcQuests.remove(state.getQuestGiver(), state);
        }
    }
}
