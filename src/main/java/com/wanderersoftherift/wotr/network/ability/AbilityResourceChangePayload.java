package com.wanderersoftherift.wotr.network.ability;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.AbilityResource;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

/**
 * Payload to transmit a change in mana value to a client. Clients simulate regen/degen, so only need to send for major
 * changes
 * 
 * @param newValue The new mana value to set
 */
public record AbilityResourceChangePayload(Holder<AbilityResource> resource, float newValue)
        implements CustomPacketPayload {

    public static final Type<AbilityResourceChangePayload> TYPE = new Type<>(
            WanderersOfTheRift.id("ability_resource_change"));

    public static final StreamCodec<RegistryFriendlyByteBuf, AbilityResourceChangePayload> STREAM_CODEC = StreamCodec
            .composite(
                    ByteBufCodecs.holderRegistry(WotrRegistries.Keys.ABILITY_RESOURCES),
                    AbilityResourceChangePayload::resource, ByteBufCodecs.FLOAT, AbilityResourceChangePayload::newValue,
                    AbilityResourceChangePayload::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handleOnClient(IPayloadContext context) {
        context.player().getData(WotrAttachments.ABILITY_RESOURCE_DATA).setAmount(resource, newValue);
    }
}
