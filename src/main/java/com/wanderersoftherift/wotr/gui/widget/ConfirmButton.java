package com.wanderersoftherift.wotr.gui.widget;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

/**
 * Button that has to be clicked twice to confirm its action
 */
public class ConfirmButton extends Button {
    private final Component normalLabel;
    private final Component confirmLabel;
    private final OnPress onConfirm;
    private boolean confirm;

    public ConfirmButton(int x, int y, int width, int height, Component normalLabel, Component confirmLabel,
            OnPress onConfirm) {
        super(x, y, width, height, normalLabel, ConfirmButton::onPress, Button.DEFAULT_NARRATION);
        this.normalLabel = normalLabel;
        this.confirmLabel = confirmLabel;
        this.onConfirm = onConfirm;
    }

    public void reset() {
        confirm = false;
        setMessage(normalLabel);
    }

    private static void onPress(Button button) {
        if (!(button instanceof ConfirmButton confirmButton)) {
            return;
        }
        confirmButton.handlePress();
    }

    private void handlePress() {
        if (confirm) {
            onConfirm.onPress(this);
            confirm = false;
            setMessage(normalLabel);
        } else {
            confirm = true;
            setMessage(confirmLabel);
        }
    }
}
