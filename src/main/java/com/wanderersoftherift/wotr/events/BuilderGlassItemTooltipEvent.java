package com.dimensiondelvers.dimensiondelvers.events;

import com.dimensiondelvers.dimensiondelvers.DimensionDelvers;
import com.dimensiondelvers.dimensiondelvers.init.ModItems;
import com.mojang.datafixers.util.Either;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;

import java.util.List;

@EventBusSubscriber(modid = DimensionDelvers.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
public class BuilderGlassItemTooltipEvent {
    @SubscribeEvent
    public static void on(RenderTooltipEvent.GatherComponents event) {
        List<Either<FormattedText, TooltipComponent>> list = event.getTooltipElements();
        ItemStack stack = event.getItemStack();
        if (!stack.is(ModItems.BUILDER_GLASSES.get())) return;
        if (!stack.has(DataComponents.CUSTOM_DATA)) return;
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null) return;
        CompoundTag tag = data.copyTag();
        if (!tag.contains("structureDim") || !tag.contains("structurePos")) return;
        String dim = tag.getString("structureDim");
        int[] pos = tag.getIntArray("structurePos");
        list.add(Either.left(FormattedText.of("Structure: " + dim + " " + pos[0] + " " + pos[1] + " " + pos[2])));
    }
}
