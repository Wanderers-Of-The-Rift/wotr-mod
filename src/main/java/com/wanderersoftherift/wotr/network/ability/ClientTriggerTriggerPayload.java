package com.wanderersoftherift.wotr.network.ability;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.attachment.TriggerTracker;
import com.wanderersoftherift.wotr.abilities.triggers.MainAttackTrigger;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClientTriggerTriggerPayload(int trigger) implements CustomPacketPayload {

    public static final Type<ClientTriggerTriggerPayload> TYPE = new Type<>(
            WanderersOfTheRift.id("trigger_client_trigger"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientTriggerTriggerPayload> STREAM_CODEC = StreamCodec
            .composite(ByteBufCodecs.INT, ClientTriggerTriggerPayload::trigger, ClientTriggerTriggerPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handleOnServer(IPayloadContext iPayloadContext) {
        if (iPayloadContext.player() instanceof ServerPlayer player) {
            switch (this.trigger()) {
                case 0 -> {
                    TriggerTracker.forEntity(player).trigger(MainAttackTrigger.INSTANCE);
                }
            }
        }
    }
}
