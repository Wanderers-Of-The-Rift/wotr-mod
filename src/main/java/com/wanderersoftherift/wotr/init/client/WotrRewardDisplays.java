package com.wanderersoftherift.wotr.init.client;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.guild.quest.reward.ItemReward;
import com.wanderersoftherift.wotr.gui.widget.lookup.RegisterRewardDisplaysEvent;
import com.wanderersoftherift.wotr.gui.widget.scrollentry.ItemRewardWidget;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = WanderersOfTheRift.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class WotrRewardDisplays {
    @SubscribeEvent
    private static void registerWidgets(RegisterRewardDisplaysEvent event) {
        event.register(ItemReward.class, ItemRewardWidget::new);
    }
}
