package com.dimensiondelvers.dimensiondelvers.events;


import com.dimensiondelvers.dimensiondelvers.DimensionDelvers;
import com.dimensiondelvers.dimensiondelvers.init.ModDataComponentType;
import com.dimensiondelvers.dimensiondelvers.item.runegem.RuneGemShape;
import com.dimensiondelvers.dimensiondelvers.item.socket.GearSocket;
import com.dimensiondelvers.dimensiondelvers.item.socket.GearSockets;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@EventBusSubscriber(modid = DimensionDelvers.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
public class GearSocketTooltipEvent {
    @SubscribeEvent
    public static void onTooltipRender(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (!stack.has(ModDataComponentType.GEAR_SOCKETS)) return;

        GearSockets sockets = stack.get(ModDataComponentType.GEAR_SOCKETS);
        if (sockets == null) return;
        List<GearSocket> socketList = sockets.sockets();

        List<Component> toAdd = new ArrayList<>();
        MutableComponent component = Component.literal("Sockets:").withStyle(Style.EMPTY.withBold(true));
        toAdd.add(component);

        for (GearSocket socket : socketList) {
            boolean hasGem = socket.runegem() != null;
            MutableComponent component1 = Component.literal(socket.runeGemShape().getName()).withStyle(Style.EMPTY.withBold(hasGem).withUnderlined(hasGem).withColor(colorMap.get(socket.runeGemShape())));
            toAdd.add(component1);
        }

        for (int i = toAdd.size() - 1; i >= 0; i--) {
            event.getToolTip().add(1, toAdd.get(i));
        }
    }

    private static final Map<RuneGemShape, ChatFormatting> colorMap = Map.of(
            RuneGemShape.CIRCLE, ChatFormatting.BLUE,
            RuneGemShape.SQUARE, ChatFormatting.YELLOW,
            RuneGemShape.TRIANGLE, ChatFormatting.GREEN,
            RuneGemShape.DIAMOND, ChatFormatting.RED,
            RuneGemShape.HEART, ChatFormatting.LIGHT_PURPLE,
            RuneGemShape.PENTAGON, ChatFormatting.DARK_PURPLE
    );
}
