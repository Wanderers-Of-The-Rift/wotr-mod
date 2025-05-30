package com.wanderersoftherift.wotr.client;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.WotrDataComponentType;
import com.wanderersoftherift.wotr.item.currency.CurrencyProvider;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(modid = WanderersOfTheRift.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
public class TooltipEvents {

    @SubscribeEvent
    public static void itemTooltipEvent(ItemTooltipEvent event) {
        CurrencyProvider currencyProvider = event.getItemStack().get(WotrDataComponentType.CURRENCY_PROVIDER);
        if (currencyProvider != null) {
            currencyProvider.addToTooltip(event.getContext(), event.getToolTip()::add, event.getFlags());
        }
    }
}
