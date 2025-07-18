package com.wanderersoftherift.wotr.gui.widget.scrollentry;

import com.wanderersoftherift.wotr.gui.widget.ScrollContainerEntry;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Widget that "flows" its child widgets, wrapping when they exceed its width
 */
public class FlowContainer extends AbstractWidget implements ScrollContainerEntry {
    private final List<AbstractWidget> children;
    private final int padding;

    public FlowContainer(Collection<AbstractWidget> children) {
        this(children, 0);
    }

    public FlowContainer(Collection<AbstractWidget> children, int padding) {
        super(0, 0, 0, 0, Component.empty());
        this.children = new ArrayList<>(children);
        this.padding = padding;
    }

    @Override
    public int getHeight(int width) {
        TotalHeightAction calcHeight = new TotalHeightAction();
        layoutChildren(width, calcHeight);
        return calcHeight.totalHeight;
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        int x = getX();
        int y = getY();
        layoutChildren(width, (child, xOffset, yOffset) -> {
            child.setPosition(x + xOffset, y + yOffset);
            child.render(guiGraphics, mouseX, mouseY, partialTick);
        });
    }

    private void layoutChildren(int width, ChildAction action) {
        int xOffset = 0;
        int yOffset = 0;
        int rowHeight = 0;
        for (AbstractWidget child : children) {
            int childWidth = child.getWidth();
            if (xOffset + childWidth > width) {
                xOffset = 0;
                yOffset += rowHeight + padding;
                rowHeight = 0;
            }
            action.apply(child, xOffset, yOffset);
            xOffset += childWidth + padding;
            rowHeight = Math.max(rowHeight, child.getHeight());
        }
    }

    protected boolean isValidClickButton(int button) {
        return false;
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {

    }

    private interface ChildAction {
        void apply(AbstractWidget child, int xOffset, int yOffset);
    }

    private static class TotalHeightAction implements ChildAction {
        private int totalHeight;

        @Override
        public void apply(AbstractWidget child, int xOffset, int yOffset) {
            totalHeight = Math.max(totalHeight, yOffset + child.getHeight());
        }
    }

}
