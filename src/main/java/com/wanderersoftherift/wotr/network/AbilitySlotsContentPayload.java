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

import java.util.List;

/**
 * Payload for transmitting the contents of the ability slots to a client
 * 
 * @param abilitySlots The items contained in the ability slots (can be {@link ItemStack#EMPTY})
 * @param selected     The currently selected slot
 */
public record AbilitySlotsContentPayload(List<ItemStack> abilitySlots, int selected) implements CustomPacketPayload {
    public static final Type<AbilitySlotsContentPayload> TYPE = new Type<>(
            WanderersOfTheRift.id("ability_slots_content"));
    public static final StreamCodec<RegistryFriendlyByteBuf, AbilitySlotsContentPayload> STREAM_CODEC = StreamCodec
            .composite(ItemStack.OPTIONAL_LIST_STREAM_CODEC, AbilitySlotsContentPayload::abilitySlots,
                    ByteBufCodecs.INT, AbilitySlotsContentPayload::selected, AbilitySlotsContentPayload::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handleOnClient(IPayloadContext context) {
        AbilitySlots playerSlots = context.player().getData(ModAttachments.ABILITY_SLOTS);
        for (int i = 0; i < abilitySlots.size() && i < playerSlots.getSlots(); i++) {
            playerSlots.setStackInSlot(i, abilitySlots.get(i));
        }
        playerSlots.setSelectedSlot(selected);
    }
}
