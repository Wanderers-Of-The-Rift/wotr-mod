package com.wanderersoftherift.wotr.gui.widget.scrollentry;

import com.wanderersoftherift.wotr.gui.widget.ScrollContainerEntry;
import com.wanderersoftherift.wotr.util.ColorUtil;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

/**
 * Widget that displays a word-wrapped block of text
 */
public class WrappedTextEntry extends AbstractWidget implements ScrollContainerEntry {

    private final Font font;
    private final int color;

    public WrappedTextEntry(Font font, Component text) {
        this(font, text, ColorUtil.OFF_BLACK);
    }

    public WrappedTextEntry(Font font, Component text, int color) {
        super(0, 0, 100, font.lineHeight, text);
        this.font = font;
        this.color = color;
    }

    @Override
    public int getHeight(int width) {
        return font.wordWrapHeight(getMessage(), width);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.drawWordWrap(this.font, getMessage(), getX(), getY(), getWidth(), color, false);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    @Override
    protected boolean isValidClickButton(int button) {
        return false;
    }
}
