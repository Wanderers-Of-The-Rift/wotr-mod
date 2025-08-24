package com.wanderersoftherift.wotr.serialization;

import com.mojang.serialization.Codec;

public class VoidCodec {
    public static final Codec<Void> INSTANCE = Codec.unit((Void) null);
}
