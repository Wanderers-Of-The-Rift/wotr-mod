package com.wanderersoftherift.wotr.client.toast;

import com.google.common.base.Preconditions;
import com.wanderersoftherift.wotr.entity.player.progression.ProgressionTrack;
import com.wanderersoftherift.wotr.util.ColorUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * Toast when the player increases a rank in a progression track
 */
public class ProgressionTrackRankToast extends SimpleToast {
    private static final int TEXT_X_PADDING = 4;
    private static final int TEXT_Y_PADDING = 7;
    private static final int ICON_X_PADDING = 8;
    private static final int ICON_Y_PADDING = 7;
    private static final int ICON_SIZE = 16;

    private final Holder<ProgressionTrack> track;
    private final Component title;
    private final Component rankTitle;
    private final int rank;

    public ProgressionTrackRankToast(Holder<ProgressionTrack> track, int rank) {
        super(true);
        Preconditions.checkArgument(rank >= 1 && rank <= track.value().rankCount());
        this.track = track;
        this.rank = rank;
        this.title = Component.translatable(track.value().toastTitleId(), ProgressionTrack.getDisplayName(track));
        this.rankTitle = ProgressionTrack.getRankTitle(track, rank);
    }

    @Override
    public void renderMessage(@NotNull GuiGraphics guiGraphics, Font font, long visibilityTime) {
        List<FormattedCharSequence> list = font.split(rankTitle,
                width() - ICON_SIZE - 2 * TEXT_X_PADDING - ICON_X_PADDING);
        Optional<ResourceLocation> icon = track.value().getRank(rank).icon();
        int xOffset = ICON_X_PADDING;
        if (icon.isPresent()) {
            guiGraphics.blit(RenderType.GUI_TEXTURED, icon.get(), xOffset, ICON_Y_PADDING, 0, 0, ICON_SIZE, ICON_SIZE,
                    ICON_SIZE, ICON_SIZE);
            xOffset += ICON_SIZE;
        }

        guiGraphics.drawString(font, title, xOffset + TEXT_X_PADDING, TEXT_Y_PADDING, ColorUtil.LIGHT_GREEN, false);
        guiGraphics.drawString(font, list.getFirst(), xOffset + TEXT_X_PADDING, font.lineHeight + TEXT_Y_PADDING,
                ChatFormatting.WHITE.getColor(), false);
    }
}
