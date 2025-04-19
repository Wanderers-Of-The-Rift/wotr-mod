package com.wanderersoftherift.wotr.gui.tooltip;

import com.mojang.datafixers.util.Either;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.client.tooltip.GearSocketTooltipRenderer;
import com.wanderersoftherift.wotr.init.ModDataComponentType;
import com.wanderersoftherift.wotr.item.runegem.RunegemShape;
import com.wanderersoftherift.wotr.item.socket.GearSocket;
import com.wanderersoftherift.wotr.item.socket.GearSockets;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@EventBusSubscriber(modid = WanderersOfTheRift.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
public class GearSocketTooltipEvent {
    private static final Map<RunegemShape, ChatFormatting> colorMap = Map.of(RunegemShape.CIRCLE, ChatFormatting.BLUE,
            RunegemShape.SQUARE, ChatFormatting.YELLOW, RunegemShape.TRIANGLE, ChatFormatting.GREEN,
            RunegemShape.DIAMOND, ChatFormatting.RED, RunegemShape.HEART, ChatFormatting.LIGHT_PURPLE,
            RunegemShape.PENTAGON, ChatFormatting.DARK_PURPLE);

    @SubscribeEvent
    public static void on(RenderTooltipEvent.GatherComponents event) {
        List<Either<FormattedText, TooltipComponent>> list = event.getTooltipElements();
        ItemStack stack = event.getItemStack();
        if (!stack.has(ModDataComponentType.GEAR_SOCKETS)) return;

        GearSockets sockets = stack.get(ModDataComponentType.GEAR_SOCKETS);
        if (sockets == null) return;
        List<GearSocket> socketList = sockets.sockets();

        List<TooltipComponent> toAdd = new ArrayList<>();
        toAdd.add(new GearSocketTooltipRenderer.GearSocketComponent(stack, socketList));

        for (GearSocket socket : socketList) {

            if (!socket.isEmpty()) {
                List<TooltipComponent> tooltipComponents = socket.modifier()
                        .get()
                        .getTooltipComponent(stack, ChatFormatting.RED);
                toAdd.addAll(tooltipComponents);
            }
        }

        for (int i = 0; i < toAdd.size(); i++) {
            list.add(i + 1, Either.right(toAdd.get(i)));
        }
    }

}
