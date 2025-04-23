package com.wanderersoftherift.wotr.gui.tooltip;

import com.mojang.datafixers.util.Either;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.ModDataComponentType;
import com.wanderersoftherift.wotr.item.implicit.GearImplicits;
import com.wanderersoftherift.wotr.modifier.ModifierInstance;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = WanderersOfTheRift.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
public class GearImplicitsTooltipEvent {

    @SubscribeEvent
    public static void on(RenderTooltipEvent.GatherComponents event) {
        List<Either<FormattedText, TooltipComponent>> list = event.getTooltipElements();
        ItemStack stack = event.getItemStack();
        if (!stack.has(ModDataComponentType.GEAR_IMPLICITS)) {
            return;
        }

        GearImplicits implicits = stack.get(ModDataComponentType.GEAR_IMPLICITS);
        if (implicits == null) {
            return;
        }
        List<ModifierInstance> modifierInstances = implicits.modifierInstances(stack, Minecraft.getInstance().level);

        list.add(1, Either.left(Component.translatable("tooltip." + WanderersOfTheRift.MODID + ".implicit")
                .withStyle(ChatFormatting.GRAY)));

        List<TooltipComponent> toAdd = new ArrayList<>();
        for (ModifierInstance modifierInstance : modifierInstances) {
            List<TooltipComponent> tooltipComponents = modifierInstance.getTooltipComponent(stack, ChatFormatting.AQUA);
            toAdd.addAll(tooltipComponents);
        }

        for (int i = 0; i < toAdd.size(); i++) {
            list.add(i + 2, Either.right(toAdd.get(i)));
        }
    }
}
