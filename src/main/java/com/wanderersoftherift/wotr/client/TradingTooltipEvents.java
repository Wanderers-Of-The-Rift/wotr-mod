package com.wanderersoftherift.wotr.client;

import com.mojang.datafixers.util.Either;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.guild.trading.Price;
import com.wanderersoftherift.wotr.init.WotrDataComponentType;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;

import java.util.List;

/**
 * Adds tooltips to display item prices
 */
@EventBusSubscriber(modid = WanderersOfTheRift.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
public class TradingTooltipEvents {

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void itemTooltipEvent(RenderTooltipEvent.GatherComponents event) {
        List<Either<FormattedText, TooltipComponent>> tooltipList = event.getTooltipElements();
        Price price = event.getItemStack().get(WotrDataComponentType.PRICE);
        if (price != null) {
            tooltipList.addAll(1, price.getTooltips());
        }
    }
}
