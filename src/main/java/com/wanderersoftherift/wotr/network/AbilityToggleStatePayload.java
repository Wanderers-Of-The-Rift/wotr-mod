package com.wanderersoftherift.wotr.network;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record AbilityToggleStatePayload(String ability_location, boolean toggle) implements CustomPacketPayload {
    public static final Type<AbilityToggleStatePayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(WanderersOfTheRift.MODID, "toggle_changed"));

    public static final StreamCodec<ByteBuf, AbilityToggleStatePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, AbilityToggleStatePayload::ability_location, ByteBufCodecs.BOOL,
            AbilityToggleStatePayload::toggle, AbilityToggleStatePayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handleOnClient(final IPayloadContext context) {
//        context.player().setData(TOGGLE_ATTACHMENTS.get(ResourceLocation.parse(toggle.ability_location())), toggle.toggle());
    }
}
