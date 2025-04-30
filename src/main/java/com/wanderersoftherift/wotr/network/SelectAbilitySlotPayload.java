package com.wanderersoftherift.wotr.network;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.attachment.AbilitySlots;
import com.wanderersoftherift.wotr.init.ModAttachments;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

/**
 * Client to server request to select an ability slot. Not very important but ensures the correct slot is selected when
 * a player rejoins or reloads the world.
 * 
 * @param slot
 */
public record SelectAbilitySlotPayload(int slot) implements CustomPacketPayload {
    public static final Type<SelectAbilitySlotPayload> TYPE = new Type<>(
            WanderersOfTheRift.id("select_ability_slot_request"));
    public static final StreamCodec<ByteBuf, SelectAbilitySlotPayload> STREAM_CODEC = StreamCodec
            .composite(ByteBufCodecs.INT, SelectAbilitySlotPayload::slot, SelectAbilitySlotPayload::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handleOnServer(IPayloadContext context) {
        AbilitySlots abilitySlots = context.player().getData(ModAttachments.ABILITY_SLOTS);
        abilitySlots.setSelectedSlot(slot);
    }
}
