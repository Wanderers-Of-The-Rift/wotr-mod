package com.wanderersoftherift.wotr.core.quest.reward;

import com.mojang.serialization.Codec;
import com.wanderersoftherift.wotr.core.quest.Reward;
import com.wanderersoftherift.wotr.serialization.DualCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.entity.player.Player;

public record HealReward(float amount) implements Reward {
    public static final DualCodec<HealReward> TYPE = new DualCodec<>(
            Codec.FLOAT.fieldOf("amount").xmap(HealReward::new, HealReward::amount),
            ByteBufCodecs.FLOAT.map(HealReward::new, HealReward::amount)
    );

    @Override
    public DualCodec<? extends Reward> getType() {
        return TYPE;
    }

    @Override
    public void apply(Player player) {
        player.heal(amount);
    }
}
