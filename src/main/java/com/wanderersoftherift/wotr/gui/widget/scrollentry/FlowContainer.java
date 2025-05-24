package com.wanderersoftherift.wotr.gui.widget.scrollentry;

import com.wanderersoftherift.wotr.gui.widget.ScrollContainerEntry;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Widget that "flows" its child widgets, wrapping when they exceed its width
 */
public class FlowContainer extends AbstractWidget implements ScrollContainerEntry {

    private final List<AbstractWidget> children;

    public FlowContainer(Collection<AbstractWidget> children) {
        super(0, 0, 0, 0, Component.empty());
        this.children = new ArrayList<>(children);
    }

    @Override
    public int getHeight(int width) {
        int result = 0;
        int rowHeight = 0;
        int rowWidth = 0;
        for (AbstractWidget child : children) {
            int childWidth = child.getWidth();
            if (rowWidth + childWidth > width) {
                result += rowHeight;
                rowWidth = childWidth;
                rowHeight = child.getHeight();
            } else {
                rowWidth += childWidth;
                rowHeight = Math.max(rowHeight, child.getHeight());
            }
        }
        result += rowHeight;
        return result;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        int x = getX();
        int y = getY();
        int maxHeight = 0;
        int xOffset = 0;
        for (AbstractWidget child : children) {
            int childWidth = child.getWidth();
            if (xOffset + childWidth > getWidth()) {
                xOffset = 0;
                y += maxHeight;
                maxHeight = 0;
            }
            child.setX(x + xOffset);
            child.setY(y);
            child.render(guiGraphics, mouseX, mouseY, partialTick);
            maxHeight = Math.max(maxHeight, child.getHeight());
            xOffset += childWidth;
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}
