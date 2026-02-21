package com.wanderersoftherift.wotr.loot;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.core.quest.Reward;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public record RewardInstantLoot(List<Reward> rewards) implements InstantLoot {
    public static final MapCodec<RewardInstantLoot> CODEC = Reward.DIRECT_CODEC.listOf()
            .fieldOf("rewards")
            .xmap(RewardInstantLoot::new, RewardInstantLoot::rewards);

    @Override
    public MapCodec<? extends InstantLoot> codec() {
        return CODEC;
    }

    @Override
    public void applyToPlayer(Player player, int count) {
        for (int i = 0; i < count; i++) {
            for (var reward : rewards) {
                reward.apply(player);
            }

        }
    }
}
