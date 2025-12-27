package com.wanderersoftherift.wotr.network.ability;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.attachment.TriggerTracker;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.init.ability.WotrTrackedAbilityTriggers;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record MainAttackPayload(boolean start) implements CustomPacketPayload {

    public static final Type<MainAttackPayload> TYPE = new Type<>(
            WanderersOfTheRift.id("trigger_client_trigger"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MainAttackPayload> STREAM_CODEC = StreamCodec
            .composite(ByteBufCodecs.BOOL, MainAttackPayload::start, MainAttackPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handleOnServer(IPayloadContext iPayloadContext) {
        if (iPayloadContext.player() instanceof ServerPlayer player) {
            var triggers = TriggerTracker.forEntity(player);
            var type = WotrTrackedAbilityTriggers.MAIN_ATTACK;
            if (triggers.hasListenersOnTrigger(type) && start) {
                var repeater = player.getData(WotrAttachments.ABILITY_REPEATER);
                repeater.addTrigger(type);
            } else {
                player.getExistingData(WotrAttachments.ABILITY_REPEATER)
                        .ifPresent(repeater -> repeater.removeTrigger(type));
            }
        }
    }
}
