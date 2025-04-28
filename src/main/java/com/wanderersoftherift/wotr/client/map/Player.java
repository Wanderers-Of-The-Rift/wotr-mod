package com.wanderersoftherift.wotr.client.map;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

// Small class to hold player data for rendering and networking
public class Player {
    public static final StreamCodec<ByteBuf, Player> PLAYER_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, player -> player.x,
            ByteBufCodecs.VAR_INT, player -> player.y,
            ByteBufCodecs.VAR_INT, player -> player.z,
            ByteBufCodecs.VAR_INT, player -> player.pitch,
            ByteBufCodecs.VAR_INT, player -> player.yaw,
            ByteBufCodecs.STRING_UTF8, player -> player.uuid,
            Player::new
    );

    public String uuid;
    public int x;
    public int y;
    public int z;
    public int pitch;
    public int yaw;

    public Player(int x, int y, int z, int pitch, int yaw, String uuid) {
        this.uuid = uuid;
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = pitch;
        this.yaw = yaw;
    }
}
