package com.wanderersoftherift.wotr.gui.widget.reward;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.quest.reward.ProgressionTrackPointReward;
import com.wanderersoftherift.wotr.entity.player.progression.ProgressionTrack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

/**
 * Display for an {@link ProgressionTrackPointReward}
 */
public class ProgressionTrackRewardWidget extends RewardWidget {
    private static final int ICON_SIZE = 16;
    private static final int AMOUNT_OFFSET = 8;

    private final Holder<ProgressionTrack> track;
    private final int amount;

    public ProgressionTrackRewardWidget(ProgressionTrackPointReward reward) {
        super(reward, 0, 0,
                Math.max(ICON_SIZE,
                        AMOUNT_OFFSET + Minecraft.getInstance().font.width(Integer.toString(reward.amount())) - 2),
                ICON_SIZE, Component.empty());
        this.track = reward.track();
        this.amount = reward.amount();
        setTooltip(Tooltip.create(Component.translatable(
                WanderersOfTheRift.translationId("tooltip", "reward.track_point"),
                ProgressionTrack.getDisplayName(track), Component.translatable(track.value().pointsId()))));
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        Font font = Minecraft.getInstance().font;
        guiGraphics.blit(RenderType::guiTextured, track.value().rewardIcon(), getX(), getY(), 0, 0, ICON_SIZE,
                ICON_SIZE, ICON_SIZE, ICON_SIZE);
        String amountText = Integer.toString(amount);
        guiGraphics.drawString(Minecraft.getInstance().font, amountText, getX() + AMOUNT_OFFSET,
                getY() + ICON_SIZE - font.lineHeight + 2, 0xFFFFFFFF, true);
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {
        // TODO
    }
}
