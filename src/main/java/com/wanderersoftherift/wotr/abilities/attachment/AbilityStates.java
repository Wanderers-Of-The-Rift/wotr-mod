package com.wanderersoftherift.wotr.abilities.attachment;

import com.mojang.serialization.Codec;
import com.wanderersoftherift.wotr.modifier.WotrEquipmentSlot;
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

    private final Set<WotrEquipmentSlot> activeSlots = new LinkedHashSet<>();

    public AbilityStates(@NotNull IAttachmentHolder holder) {
        this(holder, null);
    }

    private AbilityStates(@NotNull IAttachmentHolder holder, @Nullable Data data) {
        this.holder = holder;
        if (data != null) {
            this.activeSlots.addAll(data.activeSlots);
        }
    }

    /**
     * @return A collection of all active slots
     */
    public Collection<WotrEquipmentSlot> getActiveSlots() {
        return Collections.unmodifiableSet(activeSlots);
    }

    /**
     * Sets the active state of the given slot
     * 
     * @param slot
     * @param value
     */
    public void setActive(WotrEquipmentSlot slot, boolean value) {
        boolean changed;
        if (value) {
            changed = activeSlots.add(slot);
        } else {
            changed = activeSlots.remove(slot);
        }
        if (changed && holder instanceof ServerPlayer player) {
            PacketDistributor.sendToPlayer(player, new UpdateSlotAbilityStatePayload(slot, value));
        }
    }

    /**
     * @param slot
     * @return Whether the slot has an active ability
     */
    public boolean isActive(WotrEquipmentSlot slot) {
        return activeSlots.contains(slot);
    }

    /**
     * Clears existing state and sets all the provided slots as active
     * 
     * @param activeSlots
     */
    public void clearAndSetActive(List<WotrEquipmentSlot> activeSlots) {
        this.activeSlots.clear();
        this.activeSlots.addAll(activeSlots);
    }

    /// Serialization

    public static IAttachmentSerializer<Tag, AbilityStates> getSerializer() {
        return SERIALIZER;
    }

    private Data data() {
        return new Data(List.copyOf(activeSlots));
    }

    private record Data(List<WotrEquipmentSlot> activeSlots) {
        private static final Codec<Data> CODEC = WotrEquipmentSlot.DIRECT_CODEC.listOf()
                .xmap(Data::new, Data::activeSlots);
    }

}
