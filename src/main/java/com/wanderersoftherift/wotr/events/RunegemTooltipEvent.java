package com.wanderersoftherift.wotr.events;


import com.mojang.datafixers.util.Either;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.ModDataComponentType;
import com.wanderersoftherift.wotr.item.runegem.RunegemData;
import com.wanderersoftherift.wotr.modifier.Modifier;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = WanderersOfTheRift.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
public class RunegemTooltipEvent {
    @SubscribeEvent
    public static void on(RenderTooltipEvent.GatherComponents event) {
        List<Either<FormattedText, TooltipComponent>> list = event.getTooltipElements();
        ItemStack stack = event.getItemStack();

        RunegemData runegemData = stack.get(ModDataComponentType.RUNEGEM_DATA);
        if (runegemData == null) return;
        TagKey<Modifier> tag = runegemData.tag();

        List<MutableComponent> toAdd = new ArrayList<>();
        toAdd.add(Component.literal(tag.location().getPath()));

        for (int i = 0; i < toAdd.size(); i++) {
            list.add(i + 1, Either.left(toAdd.get(i)));
        }
    }
}
