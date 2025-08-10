package com.wanderersoftherift.wotr.network.ability;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.modifier.WotrEquipmentSlot;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

/**
 * Updates the ability state of a single equipment slot
 * 
 * @param slot   The slot
 * @param active Whether it is active or not
 */
public record UpdateSlotAbilityStatePayload(WotrEquipmentSlot slot, boolean active) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<UpdateSlotAbilityStatePayload> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(WanderersOfTheRift.MODID, "slot_ability_state_update"));

    public static final StreamCodec<RegistryFriendlyByteBuf, UpdateSlotAbilityStatePayload> STREAM_CODEC = StreamCodec
            .composite(
                    WotrEquipmentSlot.STREAM_CODEC, UpdateSlotAbilityStatePayload::slot, ByteBufCodecs.BOOL,
                    UpdateSlotAbilityStatePayload::active, UpdateSlotAbilityStatePayload::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handleOnClient(final IPayloadContext context) {
        context.player().getData(WotrAttachments.ABILITY_STATES.get()).setActive(slot, active);
    }
}
