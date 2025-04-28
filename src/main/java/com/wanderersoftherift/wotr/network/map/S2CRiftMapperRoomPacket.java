package com.wanderersoftherift.wotr.network.map;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.client.map.MapData;
import com.wanderersoftherift.wotr.client.map.MapRoom;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import org.jetbrains.annotations.NotNull;

public record S2CRiftMapperRoomPacket(MapRoom room) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<S2CRiftMapperRoomPacket> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(WanderersOfTheRift.MODID, "s2c_rift_mapper_room"));

    public static final StreamCodec<ByteBuf, S2CRiftMapperRoomPacket> STREAM_CODEC = StreamCodec.composite(
            MapRoom.MAP_ROOM_CODEC, S2CRiftMapperRoomPacket::room, S2CRiftMapperRoomPacket::new
    );

    @Override
    public CustomPacketPayload.@NotNull Type<S2CRiftMapperRoomPacket> type() {
        return TYPE;
    }

    public static class S2CRiftMapperRoomPacketHandler implements IPayloadHandler<S2CRiftMapperRoomPacket> {
        public void handle(@NotNull S2CRiftMapperRoomPacket packet, @NotNull IPayloadContext context) {
            MapData.addRoom(packet.room);
        }
    }
}
