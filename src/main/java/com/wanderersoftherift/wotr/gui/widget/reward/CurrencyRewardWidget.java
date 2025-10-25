package com.wanderersoftherift.wotr.gui.widget.reward;

import com.wanderersoftherift.wotr.core.guild.currency.Currency;
import com.wanderersoftherift.wotr.core.quest.reward.CurrencyReward;
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
 * Display for an {@link CurrencyReward}
 */
public class CurrencyRewardWidget extends RewardWidget {
    private static final int ICON_SIZE = 16;
    private static final int AMOUNT_OFFSET = 8;

    private final Holder<Currency> currency;
    private final int amount;

    public CurrencyRewardWidget(CurrencyReward reward) {
        super(reward, 0, 0,
                Math.max(ICON_SIZE,
                        AMOUNT_OFFSET + Minecraft.getInstance().font.width(Integer.toString(reward.amount())) - 2),
                ICON_SIZE, Component.empty());
        this.currency = reward.currency();
        this.amount = reward.amount();
        setTooltip(Tooltip.create(Currency.getDisplayName(currency)));
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        Font font = Minecraft.getInstance().font;
        guiGraphics.blit(RenderType::guiTextured, currency.value().icon(), getX(), getY(), 0, 0, ICON_SIZE, ICON_SIZE,
                ICON_SIZE, ICON_SIZE);
        String amountText = Integer.toString(amount);
        guiGraphics.drawString(Minecraft.getInstance().font, amountText, getX() + AMOUNT_OFFSET,
                getY() + ICON_SIZE - font.lineHeight + 2, 0xFFFFFFFF, true);
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {
        // TODO
    }
}
