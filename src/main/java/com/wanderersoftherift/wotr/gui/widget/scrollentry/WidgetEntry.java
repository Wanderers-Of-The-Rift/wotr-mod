package com.wanderersoftherift.wotr.gui.widget.scrollentry;

import com.wanderersoftherift.wotr.gui.widget.ScrollContainerEntry;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractContainerWidget;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Wraps an existing widget as a ScrollContainerEntry
 */
public class WidgetEntry extends AbstractContainerWidget implements ScrollContainerEntry {
    private final AbstractWidget child;

    public WidgetEntry(AbstractWidget child) {
        super(0, 0, child.getWidth(), child.getHeight(), child.getMessage());
        this.child = child;
    }

    @Override
    public int getHeight(int width) {
        return child.getHeight();
    }

    @Override
    protected int contentHeight() {
        return child.getHeight();
    }

    @Override
    protected double scrollRate() {
        return 0;
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        child.setX(getX());
        child.setY(getY());
        child.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {
        child.updateNarration(narrationElementOutput);
    }

    @Override
    public @NotNull List<? extends GuiEventListener> children() {
        return List.of(child);
    }
}
