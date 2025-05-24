package com.wanderersoftherift.wotr.gui.screen;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.guild.currency.Currency;
import com.wanderersoftherift.wotr.core.guild.currency.Wallet;
import com.wanderersoftherift.wotr.core.guild.trading.TradeListing;
import com.wanderersoftherift.wotr.gui.menu.TradingMenu;
import com.wanderersoftherift.wotr.gui.widget.ScrollContainerEntry;
import com.wanderersoftherift.wotr.gui.widget.ScrollContainerWidget;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.network.guild.SelectTradePayload;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.function.Consumer;

public class TradingScreen extends AbstractContainerScreen<TradingMenu> {

    private static final ResourceLocation BACKGROUND = WanderersOfTheRift
            .id("textures/gui/container/trading/background.png");

    private static final int BACKGROUND_WIDTH = 324;
    private static final int BACKGROUND_HEIGHT = 166;
    private static final int BORDER_X = 5;
    private static final int ICON_SIZE = 16;

    private ScrollContainerWidget<TradeOption> tradeOptions;
    private ScrollContainerWidget<CurrencyDisplay> currencies;

    public TradingScreen(TradingMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 324;
        this.imageHeight = 166;
        this.inventoryLabelY = this.imageHeight - 94;
        this.inventoryLabelX = 107;
        this.titleLabelX = BORDER_X;
    }

    @Override
    protected void init() {
        super.init();
        Registry<TradeListing> tradeRegistry = minecraft.level.registryAccess()
                .lookupOrThrow(WotrRegistries.Keys.TRADE_LISTING);
        tradeOptions = new ScrollContainerWidget<>(leftPos + 5, topPos + 18, 95, 140,
                tradeRegistry.stream()
                        .map(x -> new TradeOption(tradeRegistry.wrapAsHolder(x), font, this::selectTrade))
                        .toList());
        addRenderableWidget(tradeOptions);

        Wallet wallet = minecraft.player.getData(WotrAttachments.WALLET.get());
        currencies = new ScrollContainerWidget<>(leftPos + 276, topPos + 18, 43, 140,
                wallet.availableCurrencies().stream().map(x -> new CurrencyDisplay(font, wallet, x)).toList());
        addRenderableWidget(currencies);
    }

    private void selectTrade(Holder<TradeListing> trade) {
        PacketDistributor.sendToServer(new SelectTradePayload(trade));
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
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
            guiGraphics.blit(RenderType::guiTextured, currency.value().icon(), getX(), getY(), 0, 0, ICON_SIZE,
                    ICON_SIZE, ICON_SIZE, ICON_SIZE);

            int amount = wallet.get(currency);
            String amountString = Integer.toString(amount);
            guiGraphics.drawString(font, amountString, getX() + 10, getY() + ICON_SIZE - font.lineHeight,
                    ChatFormatting.WHITE.getColor(), true);

            isHovered = mouseX >= getX() && mouseX < getX() + ICON_SIZE && mouseY >= getY()
                    && mouseY < getY() + ICON_SIZE;
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

        }
    }

    private static class TradeOption extends AbstractButton implements ScrollContainerEntry {

        private static final ResourceLocation BUTTON = WanderersOfTheRift
                .id("textures/gui/container/ability_bench/choice_button.png");
        private static final ResourceLocation SELECTED_BUTTON = WanderersOfTheRift
                .id("textures/gui/container/ability_bench/selected_choice_button.png");
        private static final ResourceLocation HOVERED_BUTTON = WanderersOfTheRift
                .id("textures/gui/container/ability_bench/hovered_choice_button.png");

        private final Holder<TradeListing> listing;
        private final Font font;
        private final Consumer<Holder<TradeListing>> onSelect;

        public TradeOption(Holder<TradeListing> listing, Font font, Consumer<Holder<TradeListing>> onSelect) {
            super(0, 0, 0, 0, Component.empty());
            this.listing = listing;
            this.font = font;
            this.onSelect = onSelect;
        }

        @Override
        public int getHeight(int width) {
            return 21;
        }

        @Override
        public void onPress() {
            onSelect.accept(listing);
        }

        @Override
        protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
            guiGraphics.blit(RenderType::guiTextured,
                    ResourceLocation.withDefaultNamespace("textures/gui/sprites/container/villager/trade_arrow.png"),
                    getX() + getWidth() - 36, getY() + 2, 0, 0, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);
            guiGraphics.renderFakeItem(listing.value().getOutputItem(), getX() + getWidth() - ICON_SIZE - 2,
                    getY() + 2);
            guiGraphics.renderItemDecorations(font, listing.value().getOutputItem(),
                    getX() + getWidth() - ICON_SIZE - 2, getY() + 2);
            int xOffset = getX() + 3;
            for (Object2IntMap.Entry<Holder<Currency>> entry : listing.value().getPrice().object2IntEntrySet()) {
                String cost = Integer.toString(entry.getIntValue());
                guiGraphics.drawString(font, cost, xOffset, getY() + (21 - font.lineHeight) / 2,
                        ChatFormatting.WHITE.getColor(), true);
                xOffset += font.width(cost) + 2;

                guiGraphics.blit(RenderType::guiTextured, entry.getKey().value().icon(), xOffset, getY() + 2, 0, 0,
                        ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);
                xOffset += ICON_SIZE + 2;
            }
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

        }
    }
}
