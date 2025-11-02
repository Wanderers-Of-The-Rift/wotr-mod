package com.wanderersoftherift.wotr.gui.widget.reward;

import com.wanderersoftherift.wotr.core.quest.Reward;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;

/**
 * Base type for widgets for displaying Rewards. This provides the ability to register a click handler to the widgets if
 * desired.
 */
public abstract class RewardWidget extends AbstractWidget {

    protected final Reward reward;
    private Consumer<Reward> clickHandler;

    public RewardWidget(Reward reward, int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
        this.reward = reward;
    }

    /**
     * Sets a handler for when the reward widget is clicked
     * 
     * @param handler
     */
    public void setClickListener(Consumer<Reward> handler) {
        this.clickHandler = handler;
    }

    @Override
    protected boolean isValidClickButton(int button) {
        return button == GLFW.GLFW_MOUSE_BUTTON_1 && clickHandler != null;
    }

    @Override
    public void onClick(double mouseX, double mouseY, int button) {
        if (clickHandler != null) {
            clickHandler.accept(reward);
        }
    }
}
