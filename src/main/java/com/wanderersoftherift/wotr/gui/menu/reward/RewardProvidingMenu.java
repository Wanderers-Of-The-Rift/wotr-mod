package com.wanderersoftherift.wotr.gui.menu.reward;

import net.minecraft.world.entity.player.Player;

public interface RewardProvidingMenu {

    /**
     * Server-side claim a reward from the menu
     * 
     * @param player
     * @param rewardId
     */
    void claimReward(Player player, int rewardId);
}
