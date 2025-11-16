package com.wanderersoftherift.wotr.gui.widget.scrollentry;

import com.wanderersoftherift.wotr.gui.widget.ScrollContainerEntry;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractContainerWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Renders its content with a border and an optional background
 */
public class PanelContainer extends AbstractContainerWidget implements ScrollContainerEntry {
    private final ScrollContainerEntry contents;
    private final ResourceLocation backgroundSprite;
    private final int leftBorder;
    private final int rightBorder;
    private final int topBorder;
    private final int bottomBorder;

    public PanelContainer(ScrollContainerEntry contents, int border) {
        this(contents, null, border);
    }

    public PanelContainer(ScrollContainerEntry contents, ResourceLocation backgroundSprite, int border) {
        this(contents, backgroundSprite, border, border, border, border);
    }

    public PanelContainer(ScrollContainerEntry contents, ResourceLocation backgroundSprite, int leftBorder,
            int topBorder, int rightBorder, int bottomBorder) {
        super(0, 0, leftBorder + rightBorder, topBorder + bottomBorder, Component.empty());
        this.contents = contents;
        this.backgroundSprite = backgroundSprite;
        this.leftBorder = leftBorder;
        this.topBorder = topBorder;
        this.rightBorder = rightBorder;
        this.bottomBorder = bottomBorder;
    }

    @Override
    public int getHeight(int width) {
        return topBorder + bottomBorder + contents.getHeight(width - leftBorder - rightBorder);
    }

    @Override
    protected int contentHeight() {
        return getHeight(getWidth());
    }

    @Override
    protected double scrollRate() {
        return 0;
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (backgroundSprite != null) {
            guiGraphics.blitSprite(RenderType::guiTexturedOverlay, backgroundSprite, getX(), getY(), getWidth(),
                    getHeight());
        }
        contents.setWidth(getWidth() - leftBorder - rightBorder);
        contents.setHeight(getHeight() - topBorder - bottomBorder);
        contents.setX(getX() + leftBorder);
        contents.setY(getY() + topBorder);
        contents.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {

    }

    @Override
    public @NotNull List<? extends GuiEventListener> children() {
        return List.of(contents);
    }
}
