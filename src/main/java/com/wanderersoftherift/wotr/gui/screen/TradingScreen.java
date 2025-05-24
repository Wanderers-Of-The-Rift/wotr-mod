package com.wanderersoftherift.wotr.gui.screen;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.guild.currency.Currency;
import com.wanderersoftherift.wotr.core.guild.trading.TradeOffering;
import com.wanderersoftherift.wotr.gui.menu.TradingMenu;
import com.wanderersoftherift.wotr.gui.widget.ScrollContainerEntry;
import com.wanderersoftherift.wotr.gui.widget.ScrollContainerWidget;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.function.Consumer;

public class TradingScreen extends AbstractContainerScreen<TradingMenu> {

    private static final ResourceLocation BACKGROUND = WanderersOfTheRift
            .id("textures/gui/container/trading/background.png");

    private static final int BACKGROUND_WIDTH = 276;
    private static final int BACKGROUND_HEIGHT = 166;
    private static final int BORDER_X = 5;

    private ScrollContainerWidget<TradeOption> tradeOptions;

    public TradingScreen(TradingMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 276;
        this.imageHeight = 166;
        this.inventoryLabelY = this.imageHeight - 94;
        this.inventoryLabelX = 107;
        this.titleLabelX = BORDER_X;
    }

    @Override
    protected void init() {
        super.init();
        Registry<TradeOffering> tradeRegistry = minecraft.level.registryAccess()
                .lookupOrThrow(WotrRegistries.Keys.TRADE_OFFERINGS);
        tradeOptions = new ScrollContainerWidget<>(leftPos + 5, topPos + 18, 95, 140,
                tradeRegistry.stream()
                        .map(x -> new TradeOption(tradeRegistry.wrapAsHolder(x), font, this::selectTrade))
                        .toList());
        addRenderableWidget(tradeOptions);
    }

    private void selectTrade(Holder<TradeOffering> trade) {

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

    private static class TradeOption extends AbstractButton implements ScrollContainerEntry {

        private static final ResourceLocation BUTTON = WanderersOfTheRift
                .id("textures/gui/container/ability_bench/choice_button.png");
        private static final ResourceLocation SELECTED_BUTTON = WanderersOfTheRift
                .id("textures/gui/container/ability_bench/selected_choice_button.png");
        private static final ResourceLocation HOVERED_BUTTON = WanderersOfTheRift
                .id("textures/gui/container/ability_bench/hovered_choice_button.png");

        private final Holder<TradeOffering> offering;
        private final Font font;
        private final Consumer<Holder<TradeOffering>> onSelect;

        public TradeOption(Holder<TradeOffering> offering, Font font, Consumer<Holder<TradeOffering>> onSelect) {
            super(0, 0, 0, 0, Component.empty());
            this.offering = offering;
            this.font = font;
            this.onSelect = onSelect;
        }

        @Override
        public int getHeight(int width) {
            return 21;
        }

        @Override
        public void onPress() {
            onSelect.accept(offering);
        }

        @Override
        protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
            guiGraphics.blit(RenderType::guiTextured,
                    ResourceLocation.withDefaultNamespace("textures/gui/sprites/container/villager/trade_arrow.png"),
                    getX() + getWidth() - 36, getY() + 2, 0, 0, 16, 16, 16, 16);
            guiGraphics.renderFakeItem(offering.value().getOutputItem(), getX() + getWidth() - 18, getY() + 2);
            int xOffset = getX() + 3;
            for (Object2IntMap.Entry<Holder<Currency>> entry : offering.value().getPrice().object2IntEntrySet()) {
                String cost = Integer.toString(entry.getIntValue());
                guiGraphics.drawString(font, cost, xOffset, getY() + (21 - font.lineHeight) / 2,
                        ChatFormatting.WHITE.getColor(), true);
                xOffset += font.width(cost) + 2;

                guiGraphics.blit(RenderType::guiTextured, entry.getKey().value().icon(), xOffset, getY() + 2, 0, 0, 16,
                        16, 16, 16);
                xOffset += 18;
            }
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

        }
    }
}
