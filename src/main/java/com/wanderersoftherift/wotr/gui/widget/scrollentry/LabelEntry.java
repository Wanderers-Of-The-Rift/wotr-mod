package com.wanderersoftherift.wotr.gui.widget.scrollentry;

import com.wanderersoftherift.wotr.gui.widget.ScrollContainerEntry;
import com.wanderersoftherift.wotr.util.ColorUtil;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

/**
 * Widget that renders a label
 */
public class LabelEntry extends AbstractWidget implements ScrollContainerEntry {

    private final Font font;
    private final int lineSpace;
    private final int color;

    public LabelEntry(Font font, Component label, int lineSpace) {
        this(font, label, lineSpace, ColorUtil.OFF_BLACK);
    }

    public LabelEntry(Font font, Component label, int lineSpace, int color) {
        super(0, 0, 100, font.lineHeight + lineSpace, label);
        this.font = font;
        this.lineSpace = lineSpace;
        this.color = color;
    }

    @Override
    public int getHeight(int width) {
        return font.lineHeight + lineSpace;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.drawString(this.font, getMessage(), getX(), getY(), color, false);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    @Override
    protected boolean isValidClickButton(int button) {
        return false;
    }
}
