package com.wanderersoftherift.wotr.client.tooltip;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

public record ImageComponent(Component base, ResourceLocation asset) implements TooltipComponent {
}
