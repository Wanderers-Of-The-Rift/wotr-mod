package com.wanderersoftherift.wotr.network.ability;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.sources.AbilitySource;
import com.wanderersoftherift.wotr.init.WotrAttachments;
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
 * @param source Source of the ability
 * @param active Whether it is active or not
 */
public record UpdateSlotAbilityStatePayload(AbilitySource source, boolean active) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<UpdateSlotAbilityStatePayload> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(WanderersOfTheRift.MODID, "slot_ability_state_update"));

    public static final StreamCodec<RegistryFriendlyByteBuf, UpdateSlotAbilityStatePayload> STREAM_CODEC = StreamCodec
            .composite(
                    AbilitySource.STREAM_CODEC, UpdateSlotAbilityStatePayload::source, ByteBufCodecs.BOOL,
                    UpdateSlotAbilityStatePayload::active, UpdateSlotAbilityStatePayload::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handleOnClient(final IPayloadContext context) {
        context.player().getData(WotrAttachments.ABILITY_STATES.get()).setActive(source, active);
    }
}
