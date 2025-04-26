package com.wanderersoftherift.wotr.network;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.attachment.AbilitySlots;
import com.wanderersoftherift.wotr.init.ModAttachments;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

/**
 * Payload that updates the contents of a single ability slot for a client
 * 
 * @param slot  The slot to update
 * @param stack The new contents of the slot - may be {@link ItemStack#EMPTY}
 */
public record AbilitySlotsUpdatePayload(int slot, ItemStack stack) implements CustomPacketPayload {
    public static final Type<AbilitySlotsUpdatePayload> TYPE = new Type<>(
            WanderersOfTheRift.id("ability_slots_update"));
    public static final StreamCodec<RegistryFriendlyByteBuf, AbilitySlotsUpdatePayload> STREAM_CODEC = StreamCodec
            .composite(ByteBufCodecs.INT, AbilitySlotsUpdatePayload::slot, ItemStack.OPTIONAL_STREAM_CODEC,
                    AbilitySlotsUpdatePayload::stack, AbilitySlotsUpdatePayload::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handleOnClient(IPayloadContext context) {
        AbilitySlots abilitySlots = context.player().getData(ModAttachments.ABILITY_SLOTS);
        if (slot >= 0 && slot < abilitySlots.getSlots()) {
            abilitySlots.setStackInSlot(slot, stack);
        }
    }
}
