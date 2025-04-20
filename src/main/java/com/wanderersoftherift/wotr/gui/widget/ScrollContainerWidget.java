package com.wanderersoftherift.wotr.gui.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractContainerWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * ScrollContainerWidget provides a vertically scrollable area for entries of potentially variable height. <br/>
 * To enable scrolling the containing screen/widget will need to forward:
 * <ul>
 * <li>{@link net.minecraft.client.gui.components.AbstractWidget#mouseScrolled(double, double, double, double)}</li>
 * <li>{@link net.minecraft.client.gui.components.AbstractWidget#mouseDragged(double, double, int, double, double)}</li>
 * <li>{@link net.minecraft.client.gui.components.AbstractWidget#keyPressed(int, int, int)}</li>
 * </ul>
 */
public class ScrollContainerWidget<T extends ScrollContainerEntry> extends AbstractContainerWidget
        implements Renderable, GuiEventListener, NarratableEntry {

    public static final int SCROLLBAR_SPACE = SCROLLBAR_WIDTH + 2;

    private final List<T> children = new ArrayList<>();
    private double scrollRate = 20;

    public ScrollContainerWidget(int x, int y, int width, int height) {
        this(x, y, width, height, Collections.emptyList());
    }

    public ScrollContainerWidget(int x, int y, int width, int height, Collection<? extends T> entries) {
        super(x, y, width, height, Component.empty());
        this.children.addAll(entries);
    }

    @Override
    public @NotNull List<T> children() {
        return children;
    }

    @Override
    protected int contentHeight() {
        return children.stream().map(x -> x.getHeight(width)).reduce(0, Integer::sum);
    }

    @Override
    protected double scrollRate() {
        return scrollRate;
    }

    public void setScrollRate(double scrollRate) {
        this.scrollRate = scrollRate;
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        renderList(graphics, mouseX, mouseY, delta);
        renderScrollbar(graphics);
    }

    protected void renderList(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        // Fix scroll amount if the content size has shrunk
        setScrollAmount(scrollAmount());
        graphics.enableScissor(getX(), getY(), getX() + width, getY() + height);
        int absoluteY = 0;
        for (ScrollContainerEntry child : children) {
            int rowBottom = absoluteY + child.getHeight(width - SCROLLBAR_SPACE);
            if (rowBottom >= scrollAmount() && absoluteY <= scrollAmount() + height) {
                child.setX(getX());
                child.setY(getY() + absoluteY - (int) scrollAmount());
                child.render(graphics, mouseX, mouseY, delta);
            }
            absoluteY = rowBottom;
        }
        graphics.disableScissor();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        } else if (Minecraft.getInstance().options.keyUp.matches(keyCode, scanCode)) {
            setScrollAmount(scrollAmount() - scrollRate());
            return true;
        } else if (Minecraft.getInstance().options.keyDown.matches(keyCode, scanCode)) {
            setScrollAmount(scrollAmount() + scrollRate());
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {
    }
}
