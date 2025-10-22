package com.wanderersoftherift.wotr.core.guild;

import com.wanderersoftherift.wotr.init.WotrAttachments;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber
public class GuildEventHandler {

    @SubscribeEvent
    public static void rewardRankUp(GuildEvent.RankChange event) {
        for (int rank = event.oldRank() + 1; rank <= event.newRank(); rank++) {
            event.player().getData(WotrAttachments.UNCLAIMED_GUILD_REWARDS).addReward(event.guild(), rank);
        }
    }
}
