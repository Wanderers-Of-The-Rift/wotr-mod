package com.wanderersoftherift.wotr.mixin;

import com.wanderersoftherift.wotr.client.tooltip.FixedLeftPositioner;
import com.wanderersoftherift.wotr.mixinextension.WotrGuiGraphics;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.ClientHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

@Mixin(GuiGraphics.class)
public abstract class MixinGuiGraphics implements WotrGuiGraphics {

    @Shadow
    private ItemStack tooltipStack;

    @Unique public void wotr$RenderTooltipLeft(
            Font font,
            List<Component> textComponents,
            Optional<TooltipComponent> tooltipComponent,
            ItemStack stack,
            int mouseX,
            int mouseY,
            @Nullable ResourceLocation backgroundTexture) {
        this.tooltipStack = stack;
        this.wotr$RenderTooltipLeft(font, textComponents, tooltipComponent, mouseX, mouseY, backgroundTexture);
        this.tooltipStack = ItemStack.EMPTY;
    }

    @Unique public void wotr$RenderTooltipLeft(
            Font font,
            List<Component> tooltipLines,
            Optional<TooltipComponent> visualTooltipComponent,
            int mouseX,
            int mouseY,
            @Nullable ResourceLocation sprite) {
        List<ClientTooltipComponent> list = ClientHooks.gatherTooltipComponents(this.tooltipStack, tooltipLines,
                visualTooltipComponent, mouseX, this.guiWidth(), this.guiHeight(), font);
        this.renderTooltipInternal(font, list, mouseX, mouseY, FixedLeftPositioner.INSTANCE, sprite);
    }

    @Shadow
    private void renderTooltipInternal(
            Font font,
            List<ClientTooltipComponent> tooltipLines,
            int mouseX,
            int mouseY,
            ClientTooltipPositioner tooltipPositioner,
            @Nullable ResourceLocation sprite) {
    }

    @Shadow
    public int guiWidth() {
        throw new AssertionError();
    }

    @Shadow
    public int guiHeight() {
        throw new AssertionError();
    }
}
