package com.wanderersoftherift.wotr.client.toast;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import org.jetbrains.annotations.NotNull;

/**
 * Base toast implementation that renders the standard background, lasts 5 seconds and optionally plays a sound.
 */
public abstract class SimpleToast implements Toast {
    private static final ResourceLocation BACKGROUND_SPRITE = ResourceLocation
            .withDefaultNamespace("toast/advancement");
    private static final int DISPLAY_TIME = 5000;

    private boolean playedSound;
    private Toast.Visibility wantedVisibility = Toast.Visibility.HIDE;

    public SimpleToast(boolean playSound) {
        this.playedSound = !playSound;
    }

    @Override
    public @NotNull Visibility getWantedVisibility() {
        return wantedVisibility;
    }

    @Override
    public void update(@NotNull ToastManager toastManager, long visibilityTime) {
        if (!this.playedSound && visibilityTime > 0L) {
            this.playedSound = true;
            toastManager.getMinecraft()
                    .getSoundManager()
                    .play(SimpleSoundInstance.forUI(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 1.0F, 1.0F));
        }

        if (visibilityTime >= DISPLAY_TIME * toastManager.getNotificationDisplayTimeMultiplier()) {
            wantedVisibility = Visibility.HIDE;
        } else {
            wantedVisibility = Visibility.SHOW;
        }
    }

    @Override
    public final void render(@NotNull GuiGraphics guiGraphics, @NotNull Font font, long visibilityTime) {
        renderBackground(guiGraphics);
        renderMessage(guiGraphics, font, visibilityTime);
    }

    protected abstract void renderMessage(@NotNull GuiGraphics guiGraphics, Font font, long visibilityTime);

    private void renderBackground(GuiGraphics guiGraphics) {
        guiGraphics.blitSprite(RenderType::guiTextured, BACKGROUND_SPRITE, 0, 0, this.width(), this.height());
    }
}
