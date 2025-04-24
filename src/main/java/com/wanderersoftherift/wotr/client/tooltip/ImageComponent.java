package com.wanderersoftherift.wotr.client.tooltip;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

public record ImageComponent(ItemStack stack, Component base, ResourceLocation asset) implements TooltipComponent {
}
