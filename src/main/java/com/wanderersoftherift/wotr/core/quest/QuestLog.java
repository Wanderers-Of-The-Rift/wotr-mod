package com.wanderersoftherift.wotr.core.quest;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.Holder;
import net.minecraft.util.ExtraCodecs;

import java.util.Map;

/**
 * Tracks the completion counts of quests by a player
 */
public class QuestLog {
    public static final Codec<QuestLog> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.unboundedMap(Quest.CODEC, ExtraCodecs.NON_NEGATIVE_INT)
                    .fieldOf("log")
                    .forGetter(QuestLog::getQuestCounts)
    ).apply(instance, QuestLog::new));

    private final Object2IntMap<Holder<Quest>> questCounts = new Object2IntOpenHashMap<>();

    public QuestLog() {
    }

    public QuestLog(Map<Holder<Quest>, Integer> data) {
        questCounts.putAll(data);
    }

    /**
     * @param quest
     * @return How many times the given quest has been completed
     */
    public int getCompletionCount(Holder<Quest> quest) {
        return questCounts.getOrDefault(quest, 0);
    }

    /**
     * Increments the number of times the given quest has been completed
     * 
     * @param quest
     */
    public void incrementCompletionCount(Holder<Quest> quest) {
        questCounts.mergeInt(quest, 1, Integer::sum);
    }

    /**
     * @return Complete completion counts of quests by the player
     */
    public Object2IntMap<Holder<Quest>> getQuestCounts() {
        return Object2IntMaps.unmodifiable(questCounts);
    }
}
