package com.wanderersoftherift.wotr.abilities.attachment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.sources.AbilitySource;
import com.wanderersoftherift.wotr.network.ability.UpdateSlotAbilityStatePayload;
import com.wanderersoftherift.wotr.serialization.AttachmentSerializerFromDataCodec;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Tracks the state of the abilities for each equipment slot, and replicates to holder clients
 */
public class AbilityStates {

    private static final AttachmentSerializerFromDataCodec<Data, AbilityStates> SERIALIZER = new AttachmentSerializerFromDataCodec<>(
            Data.CODEC, AbilityStates::new, AbilityStates::data);

    private final IAttachmentHolder holder;

    private final Object2IntMap<AbilitySource> states = new Object2IntArrayMap<>();

    public AbilityStates(@NotNull IAttachmentHolder holder) {
        this(holder, null);
    }

    private AbilityStates(@NotNull IAttachmentHolder holder, @Nullable Data data) {
        this.holder = holder;
        states.defaultReturnValue(0);
        if (data != null) {
            this.states.putAll(data.activeSources);
        }
    }

    /**
     * @return A collection of all active slots
     */
    public Map<AbilitySource, Integer> getStates() {
        return Collections.unmodifiableMap(states);
    }

    /**
     * Sets the active state of the given slot
     * 
     * @param source
     * @param value
     */
    public void setState(AbilitySource source, int value) {
        boolean changed;
        if (value != 0) {
            changed = states.put(source, value) != value;
        } else {
            changed = states.removeInt(source) != 0;
        }
        if (changed && holder instanceof ServerPlayer player) {
            PacketDistributor.sendToPlayer(player, new UpdateSlotAbilityStatePayload(source, value));
        }
    }

    /**
     * @param source
     * @return Whether the slot has an active ability
     */
    public boolean isActive(AbilitySource source) {
        return states.getInt(source) != 0;
    }

    /**
     * Get the state of a given ability source
     * 
     * @param source
     * @return The state of the ability linked to the given source
     */
    public int getState(AbilitySource source) {
        return states.getInt(source);
    }

    /**
     * Clears existing state and sets all the provided slots as active
     * 
     * @param activeSources
     */
    public void clearAndSetActive(Map<AbilitySource, Integer> activeSources) {
        this.states.clear();
        this.states.putAll(activeSources);
    }

    /// Serialization

    public static IAttachmentSerializer<Tag, AbilityStates> getSerializer() {
        return SERIALIZER;
    }

    private Data data() {
        return new Data(Map.copyOf(states));
    }

    private record Data(Map<AbilitySource, Integer> activeSources) {
        private static final Codec<Data> CODEC = DataEntry.CODEC.listOf()
                .xmap(dataEntries -> new Data(
                        dataEntries.stream().collect(Collectors.toMap(DataEntry::source, DataEntry::state))),
                        data -> data.activeSources.entrySet()
                                .stream()
                                .map(entry -> new DataEntry(entry.getKey(), entry.getValue()))
                                .toList());
    }

    private record DataEntry(AbilitySource source, int state) {
        private static final Codec<DataEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                AbilitySource.DIRECT_CODEC.fieldOf("source").forGetter(DataEntry::source),
                Codec.INT.fieldOf("state").forGetter(DataEntry::state)
        ).apply(instance, DataEntry::new));
    }

}
