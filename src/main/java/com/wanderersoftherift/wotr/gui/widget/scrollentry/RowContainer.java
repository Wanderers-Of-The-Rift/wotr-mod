package com.wanderersoftherift.wotr.gui.widget.scrollentry;

import com.google.common.collect.ImmutableList;
import com.wanderersoftherift.wotr.gui.widget.ScrollContainerEntry;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractContainerWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Renders list of entries, one per row
 */
public class RowContainer extends AbstractContainerWidget implements ScrollContainerEntry {

    private final List<ScrollContainerEntry> rows;

    public RowContainer(List<ScrollContainerEntry> rows) {
        super(0, 0, 0, 0, Component.empty());
        this.rows = ImmutableList.copyOf(rows);
    }

    @Override
    public int getHeight(int width) {
        return rows.stream().mapToInt(x -> x.getHeight(width)).sum();
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
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        int offset = 0;
        for (ScrollContainerEntry row : rows) {
            row.setX(getX());
            row.setY(getY() + offset);
            row.setWidth(getWidth());
            row.render(guiGraphics, mouseX, mouseY, partialTick);
            offset += row.getHeight(getWidth());
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        // TODO
    }

    @Override
    public @NotNull List<? extends GuiEventListener> children() {
        return rows;
    }
}
