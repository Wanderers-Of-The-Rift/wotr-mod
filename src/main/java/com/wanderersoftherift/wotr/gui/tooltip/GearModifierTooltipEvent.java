package com.wanderersoftherift.wotr.gui.tooltip;

import com.mojang.datafixers.util.Either;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.modifier.ModifierProvider;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import org.joml.Math;

import java.util.List;

@EventBusSubscriber(modid = WanderersOfTheRift.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
public class GearModifierTooltipEvent {

    @SubscribeEvent
    public static void onAll(RenderTooltipEvent.GatherComponents event) {
        List<Either<FormattedText, TooltipComponent>> list = event.getTooltipElements();
        ItemStack stack = event.getItemStack();

        var modifierProviders = stack.getAllOfType(ModifierProvider.class);

        modifierProviders.forEach(it -> list.addAll(Math.min(1, list.size()),
                it.tooltips(event.getMaxWidth() != -1 ? event.getMaxWidth() : event.getScreenWidth())));
    }
}
