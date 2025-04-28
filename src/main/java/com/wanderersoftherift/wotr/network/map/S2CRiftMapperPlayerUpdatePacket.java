package com.wanderersoftherift.wotr.network.map;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.client.map.MapCell;
import com.wanderersoftherift.wotr.client.map.MapData;
import com.wanderersoftherift.wotr.client.map.MapRoom;
import com.wanderersoftherift.wotr.client.map.Player;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public record S2CRiftMapperPlayerUpdatePacket(List<Player> players) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<S2CRiftMapperPlayerUpdatePacket> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(WanderersOfTheRift.MODID, "s2c_rift_mapper_player_update"));

    public static final StreamCodec<ByteBuf, List<Player>> PLAYERS_CODEC = StreamCodec.of(
            (buf, players) -> {
                buf.writeInt(players.size());
                for (Player player : players) {
                    Player.PLAYER_CODEC.encode(buf, player);
                }
            },
            buf -> {
                int size = buf.readInt();
                List<Player> players = new ArrayList<>();
                for (int i = 0; i < size; i++) {
                    players.add(Player.PLAYER_CODEC.decode(buf));
                }
                return players;
            }
    );

    public static final StreamCodec<ByteBuf, S2CRiftMapperPlayerUpdatePacket> STREAM_CODEC = StreamCodec.composite(
            PLAYERS_CODEC, S2CRiftMapperPlayerUpdatePacket::players, S2CRiftMapperPlayerUpdatePacket::new
    );

    @Override
    public CustomPacketPayload.@NotNull Type<S2CRiftMapperPlayerUpdatePacket> type() {
        return TYPE;
    }

    public static class S2CRiftMapperPlayerUpdatePacketHandler implements IPayloadHandler<S2CRiftMapperPlayerUpdatePacket> {
        public void handle(@NotNull S2CRiftMapperPlayerUpdatePacket packet, @NotNull IPayloadContext context) {
            packet.players.forEach(MapData::updatePlayer);
        }
    }
}
