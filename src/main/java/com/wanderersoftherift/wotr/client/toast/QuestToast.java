package com.wanderersoftherift.wotr.client.toast;

import com.wanderersoftherift.wotr.core.quest.Quest;
import com.wanderersoftherift.wotr.util.ColorUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * A toast for when a quest is completed
 */
public class QuestToast implements Toast {
    private static final ResourceLocation BACKGROUND_SPRITE = ResourceLocation
            .withDefaultNamespace("toast/advancement");
    private static final int TOAST_WIDTH = 160;
    private static final int PADDING = 6;
    private static final int DISPLAY_TIME = 5000;
    private static final int ICON_SIZE = 0;
    private static final Component TITLE = Component.literal("Quest Complete");

    private final Holder<Quest> quest;
    private boolean playedSound;
    private Toast.Visibility wantedVisibility = Toast.Visibility.HIDE;

    public QuestToast(Holder<Quest> quest) {
        this.quest = quest;
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
    public void render(GuiGraphics guiGraphics, Font font, long visibilityTime) {
        guiGraphics.blitSprite(RenderType::guiTextured, BACKGROUND_SPRITE, 0, 0, this.width(), this.height());
        List<FormattedCharSequence> list = font.split(Quest.title(quest), TOAST_WIDTH - ICON_SIZE - 2 * PADDING);
        guiGraphics.drawString(font, TITLE, PADDING + ICON_SIZE, 7, ColorUtil.LIGHT_GREEN, false);
        guiGraphics.drawString(font, list.getFirst(), PADDING + ICON_SIZE, 18, ChatFormatting.WHITE.getColor(), false);
    }
}
