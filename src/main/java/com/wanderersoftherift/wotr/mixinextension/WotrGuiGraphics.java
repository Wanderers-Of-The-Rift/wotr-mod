package com.wanderersoftherift.wotr.mixinextension;

import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public interface WotrGuiGraphics {
    void wotr$RenderTooltipLeft(Font font, List<Component> textComponents, Optional<TooltipComponent> tooltipComponent, ItemStack stack, int mouseX, int mouseY, @Nullable ResourceLocation backgroundTexture);
}
