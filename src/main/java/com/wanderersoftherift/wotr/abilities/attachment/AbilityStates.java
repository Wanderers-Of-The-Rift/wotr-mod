package com.wanderersoftherift.wotr.abilities.attachment;

import com.mojang.serialization.Codec;
import com.wanderersoftherift.wotr.abilities.AbilitySource;
import com.wanderersoftherift.wotr.network.ability.UpdateSlotAbilityStatePayload;
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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Tracks the state of the abilities for each equipment slot, and replicates to holder clients
 */
public class AbilityStates {

    private static final AttachmentSerializerFromDataCodec<Data, AbilityStates> SERIALIZER = new AttachmentSerializerFromDataCodec<>(
            Data.CODEC, AbilityStates::new, AbilityStates::data);

    private final IAttachmentHolder holder;

    private final Set<AbilitySource> activeSources = new LinkedHashSet<>();

    public AbilityStates(@NotNull IAttachmentHolder holder) {
        this(holder, null);
    }

    private AbilityStates(@NotNull IAttachmentHolder holder, @Nullable Data data) {
        this.holder = holder;
        if (data != null) {
            this.activeSources.addAll(data.activeSources);
        }
    }

    /**
     * @return A collection of all active slots
     */
    public Collection<AbilitySource> getActiveSources() {
        return Collections.unmodifiableSet(activeSources);
    }

    /**
     * Sets the active state of the given slot
     * 
     * @param source
     * @param value
     */
    public void setActive(AbilitySource source, boolean value) {
        boolean changed;
        if (value) {
            changed = activeSources.add(source);
        } else {
            changed = activeSources.remove(source);
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
        return activeSources.contains(source);
    }

    /**
     * Clears existing state and sets all the provided slots as active
     * 
     * @param activeSources
     */
    public void clearAndSetActive(List<AbilitySource> activeSources) {
        this.activeSources.clear();
        this.activeSources.addAll(activeSources);
    }

    /// Serialization

    public static IAttachmentSerializer<Tag, AbilityStates> getSerializer() {
        return SERIALIZER;
    }

    private Data data() {
        return new Data(List.copyOf(activeSources));
    }

    private record Data(List<AbilitySource> activeSources) {
        private static final Codec<Data> CODEC = AbilitySource.DIRECT_CODEC.listOf()
                .xmap(Data::new, Data::activeSources);
    }

}
