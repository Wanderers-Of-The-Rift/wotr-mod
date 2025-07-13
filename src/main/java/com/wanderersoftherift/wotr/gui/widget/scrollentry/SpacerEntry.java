package com.wanderersoftherift.wotr.gui.widget.scrollentry;

import com.wanderersoftherift.wotr.gui.widget.ScrollContainerEntry;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

/**
 * Widget that just takes up space
 */
public class SpacerEntry extends AbstractWidget implements ScrollContainerEntry {
    private final int space;

    public SpacerEntry(int space) {
        super(0, space, 0, space, Component.empty());
        this.space = space;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

    }

    @Override
    protected boolean isValidClickButton(int button) {
        return false;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    @Override
    public int getHeight(int width) {
        return space;
    }
}
