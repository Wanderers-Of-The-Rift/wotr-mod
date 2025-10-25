package com.wanderersoftherift.wotr.init.client;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.quest.reward.CurrencyReward;
import com.wanderersoftherift.wotr.core.quest.reward.ItemReward;
import com.wanderersoftherift.wotr.core.quest.reward.ReputationReward;
import com.wanderersoftherift.wotr.gui.widget.lookup.RegisterRewardDisplaysEvent;
import com.wanderersoftherift.wotr.gui.widget.reward.CurrencyRewardWidget;
import com.wanderersoftherift.wotr.gui.widget.reward.ItemRewardWidget;
import com.wanderersoftherift.wotr.gui.widget.reward.ReputationRewardWidget;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = WanderersOfTheRift.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class WotrRewardDisplays {
    @SubscribeEvent
    private static void registerWidgets(RegisterRewardDisplaysEvent event) {
        event.register(ItemReward.class, ItemRewardWidget::new);
        event.register(CurrencyReward.class, CurrencyRewardWidget::new);
        event.register(ReputationReward.class, ReputationRewardWidget::new);
    }
}
