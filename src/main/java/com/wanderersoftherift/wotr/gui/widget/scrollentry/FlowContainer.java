package com.wanderersoftherift.wotr.gui.widget.scrollentry;

import com.wanderersoftherift.wotr.gui.widget.ScrollContainerEntry;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
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
    private int lastWidth = 0;
    private List<List<AbstractWidget>> cachedRows = new ArrayList<>();
    private IntList cachedRowHeights = new IntArrayList();

    public FlowContainer(Collection<AbstractWidget> children) {
        super(0, 0, 0, 0, Component.empty());
        this.children = new ArrayList<>(children);
    }

    @Override
    public int getHeight(int width) {
        calcRows(width);
        return cachedRowHeights.intStream().reduce(0, Integer::sum);
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        int x = getX();
        int y = getY();
        calcRows(width);
        for (int row = 0; row < cachedRows.size(); row++) {
            int xOffset = 0;
            for (AbstractWidget item : cachedRows.get(row)) {
                item.setPosition(x + xOffset, y);
                item.render(guiGraphics, mouseX, mouseY, partialTick);
                xOffset += item.getWidth();
            }
            y += cachedRowHeights.getInt(row);
        }
    }

    private void calcRows(int width) {
        if (lastWidth == width) {
            return;
        }
        lastWidth = width;
        cachedRowHeights.clear();
        cachedRows = new ArrayList<>();

        int rowHeight = 0;
        int rowWidth = 0;
        List<AbstractWidget> row = new ArrayList<>();
        for (AbstractWidget child : children) {
            int childWidth = child.getWidth();
            if (rowWidth + childWidth > width) {
                cachedRows.add(row);
                cachedRowHeights.add(rowHeight);
                row = new ArrayList<>();
                rowWidth = 0;
                rowHeight = 0;
            }
            rowWidth += childWidth;
            rowHeight = Math.max(rowHeight, child.getHeight());
            row.add(child);
        }
        cachedRowHeights.add(rowHeight);
        cachedRows.add(row);
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {

    }
}
