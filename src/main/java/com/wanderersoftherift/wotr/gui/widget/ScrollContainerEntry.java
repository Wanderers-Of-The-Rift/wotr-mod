package com.wanderersoftherift.wotr.gui.widget;

import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import org.jetbrains.annotations.NotNull;

/**
 * Interface for entries to display in a scroll container widget.
 */
public interface ScrollContainerEntry extends GuiEventListener, Renderable, LayoutElement {

    /**
     * To allow for elements that can flow or resize based on available width
     * 
     * @param width The width available to render the entry
     * @return The height of the entry, given the specified width
     */
    int getHeight(int width);

    default @NotNull ScreenRectangle getRectangle() {
        return new ScreenRectangle(this.getX(), this.getY(), this.getWidth(), this.getHeight());
    }

}