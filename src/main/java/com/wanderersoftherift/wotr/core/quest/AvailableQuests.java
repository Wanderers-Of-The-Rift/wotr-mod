package com.wanderersoftherift.wotr.core.quest;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.core.npc.NpcIdentity;
import com.wanderersoftherift.wotr.serialization.GuavaCodecs;
import net.minecraft.core.Holder;

import java.util.Collections;
import java.util.List;

/**
 * Tracking of quests available to the player
 */
public class AvailableQuests {
    public static final Codec<AvailableQuests> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            GuavaCodecs.stringKeyListMultimap(NpcIdentity.CODEC, QuestState.CODEC)
                    .fieldOf("available_npc_quests")
                    .forGetter(x -> x.availableNpcQuests)
    ).apply(instance, AvailableQuests::new));

    private final ListMultimap<Holder<NpcIdentity>, QuestState> availableNpcQuests = ArrayListMultimap.create();

    public AvailableQuests() {
    }

    private AvailableQuests(ListMultimap<Holder<NpcIdentity>, QuestState> availableNpcQuests) {
        this.availableNpcQuests.putAll(availableNpcQuests);
    }

    public List<QuestState> getQuests(Holder<NpcIdentity> npc) {
        return Collections.unmodifiableList(availableNpcQuests.get(npc));
    }

    public void setQuests(Holder<NpcIdentity> npc, List<QuestState> availableQuests) {
        availableNpcQuests.removeAll(npc);
        availableNpcQuests.putAll(npc, availableQuests);
    }

    public void removeQuest(QuestState state) {
        availableNpcQuests.remove(state.getQuestGiver(), state);
    }
}
