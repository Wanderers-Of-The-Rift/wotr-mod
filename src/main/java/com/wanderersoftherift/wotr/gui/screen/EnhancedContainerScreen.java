package com.wanderersoftherift.wotr.gui.screen;

import com.wanderersoftherift.wotr.gui.widget.ScrollContainerWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * A base container screen that has been enhanced to better support widgets.
 * 
 * @param <T>
 */
public abstract class EnhancedContainerScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {

    private final List<ScrollContainerWidget<?>> scrollContainers = new ArrayList<>();

    public EnhancedContainerScreen(T menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected <U extends GuiEventListener & Renderable & NarratableEntry> @NotNull U addRenderableWidget(
            @NotNull U widget) {
        if (widget instanceof ScrollContainerWidget<?> scrollContainer) {
            scrollContainers.add(scrollContainer);
        }
        return super.addRenderableWidget(widget);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double xOffset, double yOffset) {
        if (!super.mouseScrolled(mouseX, mouseY, xOffset, yOffset)) {
            for (var scrollContainer : scrollContainers) {
                if (scrollContainer.isHovered()) {
                    return scrollContainer.mouseScrolled(mouseX, mouseY, xOffset, yOffset);
                }
            }
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        for (var scrollContainer : scrollContainers) {
            scrollContainer.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        }

        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!super.keyPressed(keyCode, scanCode, modifiers)) {
            for (var scrollContainer : scrollContainers) {
                if (scrollContainer.isHoveredOrFocused()) {
                    return scrollContainer.keyPressed(keyCode, scanCode, modifiers);
                }
            }
            return false;
        }
        return true;
    }
}
