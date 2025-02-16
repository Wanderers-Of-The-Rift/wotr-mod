package com.dimensiondelvers.dimensiondelvers.events;


import com.dimensiondelvers.dimensiondelvers.DimensionDelvers;
import com.dimensiondelvers.dimensiondelvers.init.ModDataComponentType;
import com.dimensiondelvers.dimensiondelvers.item.runegem.RunegemShape;
import com.dimensiondelvers.dimensiondelvers.item.socket.GearSocket;
import com.dimensiondelvers.dimensiondelvers.item.socket.GearSockets;
import com.dimensiondelvers.dimensiondelvers.modifier.Modifier;
import com.dimensiondelvers.dimensiondelvers.modifier.ModifierInstance;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
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
    private static final Map<RunegemShape, ChatFormatting> colorMap = Map.of(
            RunegemShape.CIRCLE, ChatFormatting.BLUE,
            RunegemShape.SQUARE, ChatFormatting.YELLOW,
            RunegemShape.TRIANGLE, ChatFormatting.GREEN,
            RunegemShape.DIAMOND, ChatFormatting.RED,
            RunegemShape.HEART, ChatFormatting.LIGHT_PURPLE,
            RunegemShape.PENTAGON, ChatFormatting.DARK_PURPLE
    );

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
            boolean hasGem = socket.runegem().isPresent();
            MutableComponent component1;
            Style style = Style.EMPTY.withBold(hasGem).withUnderlined(hasGem).withColor(colorMap.get(socket.shape()));
            if (!hasGem || socket.modifier().isEmpty()) {
                component1 = Component.literal(socket.shape().getName()).withStyle(style);
            } else {
                ModifierInstance modifierInstance = socket.modifier().get();
                Holder<Modifier> modifierHolder = modifierInstance.getModifier();
                Modifier modifier = modifierHolder.value();
                float roll = modifierInstance.getRoll();
                component1 = Component.literal(modifierHolder.getRegisteredName() + " " + roll).withStyle(style);
            }
            toAdd.add(component1);
        }

        for (int i = toAdd.size() - 1; i >= 0; i--) {
            event.getToolTip().add(1, toAdd.get(i));
        }
    }
}
