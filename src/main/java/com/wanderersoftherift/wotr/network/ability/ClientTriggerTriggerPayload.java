package com.wanderersoftherift.wotr.network.ability;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.attachment.TriggerTracker;
import com.wanderersoftherift.wotr.abilities.triggers.TrackableTrigger;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClientTriggerTriggerPayload(TrackableTrigger.TriggerType trigger) implements CustomPacketPayload {

    public static final Type<ClientTriggerTriggerPayload> TYPE = new Type<>(
            WanderersOfTheRift.id("trigger_client_trigger"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientTriggerTriggerPayload> STREAM_CODEC = StreamCodec
            .composite(ByteBufCodecs.registry(WotrRegistries.Keys.TRACKABLE_TRIGGERS),
                    ClientTriggerTriggerPayload::trigger, ClientTriggerTriggerPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handleOnServer(IPayloadContext iPayloadContext) {
        if (iPayloadContext.player() instanceof ServerPlayer player) {
            var trigger = this.trigger().clientTriggerInstance();
            if (trigger != null) {
                TriggerTracker.forEntity(player).trigger(trigger);
            }
        }
    }
}
