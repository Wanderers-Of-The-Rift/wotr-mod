package com.wanderersoftherift.wotr.client.map;

public class MapRoomEffects {
    public enum Flag {
        DOTS,
        EDGE_HIGHLIGHT
    }

    public static int getFlags(Flag[] flags) {
        int result = 0;
        for (Flag flag : flags) {
            int id = flag.ordinal();
            result = result | (int) Math.pow(2, id);
        }
        return result;
    }
}
