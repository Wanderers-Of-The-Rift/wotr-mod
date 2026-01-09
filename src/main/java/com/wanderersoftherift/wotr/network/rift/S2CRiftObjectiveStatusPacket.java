package com.wanderersoftherift.wotr.network.rift;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.rift.objective.OngoingObjective;
import com.wanderersoftherift.wotr.gui.layer.objective.ObjectiveRenderers;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record S2CRiftObjectiveStatusPacket(Optional<OngoingObjective> objective) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<S2CRiftObjectiveStatusPacket> TYPE = new CustomPacketPayload.Type<>(
            (WanderersOfTheRift.id("s2c_rift_objective_status")));

    public static final StreamCodec<RegistryFriendlyByteBuf, S2CRiftObjectiveStatusPacket> STREAM_CODEC = StreamCodec
            .composite(
                    ByteBufCodecs.optional(ByteBufCodecs.fromCodecWithRegistries(OngoingObjective.DIRECT_CODEC)),
                    S2CRiftObjectiveStatusPacket::objective, S2CRiftObjectiveStatusPacket::new);

    @Override
    public CustomPacketPayload.@NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static class S2CRiftObjectiveStatusPacketHandler implements IPayloadHandler<S2CRiftObjectiveStatusPacket> {
        public void handle(@NotNull S2CRiftObjectiveStatusPacket packet, @NotNull IPayloadContext context) {
            ObjectiveRenderers.handleObjectiveStatus(packet);
        }
    }
}
