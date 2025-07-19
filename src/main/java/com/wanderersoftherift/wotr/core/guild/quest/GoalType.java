package com.wanderersoftherift.wotr.core.guild.quest;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

/**
 * A type for a goal - provides serialization for that goal.
 *
 * @param codec
 * @param streamCodec
 * @param <T>
 */
public record GoalType<T extends Goal>(MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {
}
