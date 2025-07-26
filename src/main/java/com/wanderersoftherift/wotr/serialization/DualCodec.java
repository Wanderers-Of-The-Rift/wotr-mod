package com.wanderersoftherift.wotr.serialization;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

/**
 * Type that combines a Codec and a StreamCodec for dispatch types that have both local and remote serialization.
 *
 * @param codec
 * @param streamCodec
 * @param <T>
 */
public record DualCodec<T>(MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {
}
