package com.wanderersoftherift.wotr.client.toast;

import com.wanderersoftherift.wotr.core.quest.Quest;
import com.wanderersoftherift.wotr.util.ColorUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

/**
 * A toast for when a quest is completed
 */
public class QuestCompleteToast extends SimpleToast {
    private static final int PADDING = 6;
    private static final int ICON_SIZE = 0;
    private static final Component TITLE = Component.literal("Quest Complete");

    private final Holder<Quest> quest;

    public QuestCompleteToast(Holder<Quest> quest) {
        super(true);
        this.quest = quest;
    }

    @Override
    public void renderMessage(GuiGraphics guiGraphics, Font font, long visibilityTime) {
        List<FormattedCharSequence> list = font.split(Quest.title(quest), width() - ICON_SIZE - 2 * PADDING);
        guiGraphics.drawString(font, TITLE, PADDING + ICON_SIZE, 7, ColorUtil.LIGHT_GREEN, false);
        guiGraphics.drawString(font, list.getFirst(), PADDING + ICON_SIZE, 18, ChatFormatting.WHITE.getColor(), false);
    }
}
