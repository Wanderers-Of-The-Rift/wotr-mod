package com.wanderersoftherift.wotr.gui.widget.quest;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.guild.Guild;
import com.wanderersoftherift.wotr.core.quest.reward.CurrencyReward;
import com.wanderersoftherift.wotr.core.quest.reward.ReputationReward;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

/**
 * Display for an {@link CurrencyReward}
 */
public class ReputationRewardWidget extends AbstractWidget {
    private static final int ICON_SIZE = 16;

    private final Holder<Guild> guild;
    private final int amount;

    public ReputationRewardWidget(ReputationReward reward) {
        super(0, 0, ICON_SIZE, ICON_SIZE, Component.empty());
        this.guild = reward.guild();
        this.amount = reward.amount();
        setTooltip(Tooltip.create(Component.translatable(
                WanderersOfTheRift.translationId("tooltip", "reward.reputation"), Guild.getDisplayName(guild))));
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        Font font = Minecraft.getInstance().font;
        guiGraphics.blit(RenderType::guiTextured, guild.value().icon16(), getX(), getY(), 0, 0, ICON_SIZE, ICON_SIZE,
                ICON_SIZE, ICON_SIZE);
        String amountText = Integer.toString(amount);
        guiGraphics.drawString(Minecraft.getInstance().font, amountText,
                getX() + ICON_SIZE + 2 - font.width(amountText), getY() + ICON_SIZE - font.lineHeight + 2, 0xFFFFFFFF,
                true);
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {
        // TODO
    }

    @Override
    protected boolean isValidClickButton(int button) {
        return false;
    }
}
