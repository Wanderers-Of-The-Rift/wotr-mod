package com.wanderersoftherift.wotr.network.ability;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import io.netty.buffer.ByteBuf;
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
public record ManaChangePayload(float newValue) implements CustomPacketPayload {

    public static final Type<ManaChangePayload> TYPE = new Type<>(WanderersOfTheRift.id("mana_change"));

    public static final StreamCodec<ByteBuf, ManaChangePayload> STREAM_CODEC = StreamCodec
            .composite(ByteBufCodecs.FLOAT, ManaChangePayload::newValue, ManaChangePayload::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handleOnClient(IPayloadContext context) {
        context.player().getData(WotrAttachments.MANA).setAmount(newValue);
    }
}
