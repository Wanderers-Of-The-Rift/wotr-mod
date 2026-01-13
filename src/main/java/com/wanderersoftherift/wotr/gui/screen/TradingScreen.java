package com.wanderersoftherift.wotr.gui.screen;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.currency.Currency;
import com.wanderersoftherift.wotr.core.currency.Wallet;
import com.wanderersoftherift.wotr.core.npc.trading.AvailableTrades;
import com.wanderersoftherift.wotr.gui.menu.TradingMenu;
import com.wanderersoftherift.wotr.gui.widget.ScrollContainerEntry;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

/**
 * A screen for trading with guilds.
 */
public class TradingScreen extends AbstractContainerScreen<TradingMenu> {

    private static final ResourceLocation BACKGROUND = WanderersOfTheRift
            .id("textures/gui/container/trading/background.png");

    private static final ResourceKey<Currency> TRADE_CURRENCY = ResourceKey.create(WotrRegistries.Keys.CURRENCIES,
            WanderersOfTheRift.id("coin"));

    private static final int BACKGROUND_WIDTH = 277;
    private static final int BACKGROUND_HEIGHT = 166;
    private static final int BORDER_X = 5;
    private static final int ICON_SIZE = 16;

    private final Holder<Currency> tradeCurrency;
    private CurrencyDisplay currencyDisplay;

    public TradingScreen(TradingMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 277;
        this.imageHeight = 166;
        this.inventoryLabelY = this.imageHeight - 94;
        this.inventoryLabelX = 107;
        this.titleLabelX = BORDER_X;
        this.tradeCurrency = Minecraft.getInstance().level.registryAccess().holderOrThrow(TRADE_CURRENCY);
    }

    @Override
    protected void init() {
        super.init();

        Wallet wallet = minecraft.player.getData(WotrAttachments.WALLET.get());
        currencyDisplay = new CurrencyDisplay(font, wallet, tradeCurrency);
        currencyDisplay.setPosition(this.leftPos + 206, this.topPos + 6);
        currencyDisplay.setWidth(65);
        currencyDisplay.setHeight(13);
        addRenderableWidget(currencyDisplay);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        for (int slot = 0; slot < AvailableTrades.MERCHANT_INVENTORY_SIZE; slot++) {
            if (!menu.canPurchase(slot, minecraft.player)) {

                int slotX = slot % TradingMenu.TRADE_SLOT_COLUMNS;
                int slotY = slot / TradingMenu.TRADE_SLOT_COLUMNS;
                int x = leftPos + 8 + 18 * slotX;
                int y = topPos + 30 + 18 * slotY;

                guiGraphics.fill(x, y, x + 16, y + 16, 0x77FF7777);
            }
        }

        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(RenderType::guiTextured, BACKGROUND, this.leftPos, this.topPos, 0, 0, this.imageWidth,
                this.imageHeight, BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
    }

    public static class CurrencyDisplay extends AbstractWidget implements ScrollContainerEntry {
        private final Font font;
        private final Wallet wallet;
        private final Holder<Currency> currency;

        public CurrencyDisplay(Font font, Wallet wallet, Holder<Currency> currency) {
            super(0, 0, 0, 0, Component.empty());
            this.font = font;
            this.wallet = wallet;
            this.currency = currency;
            setTooltip(Tooltip.create(Currency.getDisplayName(currency)));
        }

        @Override
        public int getHeight(int width) {
            return ICON_SIZE + 2;
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            return false;
        }

        @Override
        protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            guiGraphics.blit(RenderType::guiTextured, currency.value().icon(), getX() + getWidth() - ICON_SIZE, getY(),
                    0, 0, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);

            int amount = wallet.get(currency);
            String amountString = Integer.toString(amount);
            int amountWidth = font.width(amountString);
            guiGraphics.drawString(font, amountString, getX() + getWidth() - ICON_SIZE - amountWidth,
                    getY() + (ICON_SIZE - font.lineHeight) / 2, ChatFormatting.WHITE.getColor(), true);

            isHovered = mouseX >= getX() + getWidth() - ICON_SIZE && mouseX < getX() + getWidth() && mouseY >= getY()
                    && mouseY < getY() + ICON_SIZE;
        }

        @Override
        protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {
            // TODO
        }
    }
}
