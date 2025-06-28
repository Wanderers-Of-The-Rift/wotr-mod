package com.wanderersoftherift.wotr.core.guild.quest;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.ArrayList;
import java.util.List;

/**
 * Player attachment for holding the active quests of the player
 */
public final class ActiveQuests {
    public static final Codec<ActiveQuests> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ActiveQuest.CODEC.listOf().fieldOf("quests").forGetter(ActiveQuests::quests)
    ).apply(instance, ActiveQuests::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, ActiveQuests> STREAM_CODEC = ActiveQuest.STREAM_CODEC
            .apply(ByteBufCodecs.list())
            .map(ActiveQuests::new, ActiveQuests::quests);

    private final List<ActiveQuest> quests;

    public ActiveQuests() {
        this.quests = new ArrayList<>();
    }

    public ActiveQuests(List<ActiveQuest> quests) {
        this.quests = new ArrayList<>(quests);
    }

    /**
     * @return The list of active quests
     */
    public List<ActiveQuest> quests() {
        return quests;
    }

    /**
     * Removes a quest from the active quests
     * 
     * @param index
     */
    public void remove(int index) {
        quests.remove(index);
    }

    /**
     * Updates the active quests from data replicated from the server
     * 
     * @param newQuests
     */
    public void updateFromServer(List<ActiveQuest> newQuests) {
        while (quests.size() > newQuests.size()) {
            quests.removeLast();
        }
        for (int i = 0; i < newQuests.size(); i++) {
            if (i < quests.size()) {
                quests.get(i).updateFromServer(newQuests.get(i));
            } else {
                quests.addLast(newQuests.get(i));
            }
        }
    }

}
