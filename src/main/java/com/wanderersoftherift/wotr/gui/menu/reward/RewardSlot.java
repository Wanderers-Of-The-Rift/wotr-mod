package com.wanderersoftherift.wotr.gui.menu.reward;

import com.wanderersoftherift.wotr.core.quest.Reward;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/**
 * A slot containing a reward for the Reward Menu
 * 
 * @param id
 * @param reward
 */
public record RewardSlot(int id, Reward reward) {
    public static final StreamCodec<RegistryFriendlyByteBuf, RewardSlot> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, RewardSlot::id, Reward.STREAM_CODEC, RewardSlot::reward, RewardSlot::new
    );
}
