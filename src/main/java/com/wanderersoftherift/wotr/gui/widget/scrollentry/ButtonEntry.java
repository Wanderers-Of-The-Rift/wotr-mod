package com.wanderersoftherift.wotr.gui.widget.scrollentry;

import com.wanderersoftherift.wotr.gui.widget.ScrollContainerEntry;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link ScrollContainerEntry} that is a button
 */
public class ButtonEntry extends AbstractButton implements ScrollContainerEntry {

    private final Runnable action;

    public ButtonEntry(Component message, int height, Runnable action) {
        super(0, 0, 0, height, message);
        this.action = action;
    }

    @Override
    public int getHeight(int width) {
        return getHeight();
    }

    @Override
    public void onPress() {
        action.run();
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {
        defaultButtonNarrationText(narrationElementOutput);
    }
}