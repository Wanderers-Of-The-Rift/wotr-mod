package com.wanderersoftherift.wotr.network;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.ModAttachments;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Payload to transmit a change in mana value to a client. Clients simulate regen/degen, so only need to send for major
 * changes
 * 
 * @param newValue The new mana value to set
 */
public record ManaChangePayload(int newValue) implements CustomPacketPayload {

    public static final Type<ManaChangePayload> TYPE = new Type<>(WanderersOfTheRift.id("mana_change"));

    public static final StreamCodec<ByteBuf, ManaChangePayload> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.INT,
            ManaChangePayload::newValue, ManaChangePayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handleOnClient(IPayloadContext context) {
        context.player().getData(ModAttachments.MANA).setAmount(context.player(), newValue);
    }
}
