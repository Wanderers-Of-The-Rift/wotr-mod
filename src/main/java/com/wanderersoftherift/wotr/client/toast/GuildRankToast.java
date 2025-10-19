package com.wanderersoftherift.wotr.client.toast;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.guild.Guild;
import com.wanderersoftherift.wotr.core.guild.GuildRank;
import com.wanderersoftherift.wotr.util.ColorUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public class GuildRankToast extends SimpleToast {
    private static final int TEXT_X_PADDING = 4;
    private static final int TEXT_Y_PADDING = 7;
    private static final int ICON_X_PADDING = 8;
    private static final int ICON_Y_PADDING = 7;
    private static final int ICON_SIZE = 16;
    private static final Component TITLE = Component
            .translatable(WanderersOfTheRift.translationId("toast", "guild.rank"));

    private final Holder<Guild> guild;
    private final int rank;

    public GuildRankToast(Holder<Guild> guild, int rank) {
        super(true);
        this.guild = guild;
        this.rank = rank;
    }

    @Override
    public void renderMessage(GuiGraphics guiGraphics, Font font, long visibilityTime) {
        GuildRank guildRank = guild.value().ranks().get(rank - 1);
        List<FormattedCharSequence> list = font.split(Guild.getRankTitle(guild, rank),
                width() - ICON_SIZE - 2 * TEXT_X_PADDING - ICON_X_PADDING);
        guiGraphics.blit(RenderType.GUI_TEXTURED, guildRank.icon(), ICON_X_PADDING, ICON_Y_PADDING, 0, 0, ICON_SIZE,
                ICON_SIZE, ICON_SIZE, ICON_SIZE);
        guiGraphics.drawString(font, TITLE, ICON_X_PADDING + ICON_SIZE + TEXT_X_PADDING, TEXT_Y_PADDING,
                ColorUtil.LIGHT_GREEN, false);
        guiGraphics.drawString(font, list.getFirst(), ICON_X_PADDING + ICON_SIZE + TEXT_X_PADDING,
                font.lineHeight + TEXT_Y_PADDING, ChatFormatting.WHITE.getColor(), false);
    }
}
