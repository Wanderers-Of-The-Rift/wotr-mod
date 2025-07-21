package com.wanderersoftherift.wotr.core.quest;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

/**
 * A type of reward. Providers serializers for the reward.
 * 
 * @param codec
 * @param streamCodec
 * @param <T>
 */
public record RewardType<T extends Reward>(MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {
}
