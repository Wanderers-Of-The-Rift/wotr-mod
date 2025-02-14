package com.wanderersoftherift.wotr.network;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import org.jetbrains.annotations.NotNull;

public record S2CDimensionTypesUpdatePacket(
        ResourceLocation resourceLocation,
        boolean removal
) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<S2CDimensionTypesUpdatePacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(WanderersOfTheRift.MODID, "s2c_dimension_types_update"));

    public static final StreamCodec<ByteBuf, S2CDimensionTypesUpdatePacket> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC,
            S2CDimensionTypesUpdatePacket::resourceLocation,
            ByteBufCodecs.BOOL,
            S2CDimensionTypesUpdatePacket::removal,
            S2CDimensionTypesUpdatePacket::new
    );

    @Override
    public CustomPacketPayload.@NotNull Type<S2CDimensionTypesUpdatePacket> type() {
        return TYPE;
    }

    public static class S2CDimensionTypesUpdatePacketHandler implements IPayloadHandler<S2CDimensionTypesUpdatePacket> {
        public void handle(@NotNull S2CDimensionTypesUpdatePacket packet, @NotNull IPayloadContext context) {
            Player player = context.player();
            if (!(player instanceof LocalPlayer localPlayer)) {
                return;
            }

            if (packet.removal()) {
                localPlayer.connection.levels().removeIf(key -> key.location().equals(packet.resourceLocation()));
            } else {
                localPlayer.connection.levels().add(ResourceKey.create(Registries.DIMENSION, packet.resourceLocation()));
            }
        }
    }
}
