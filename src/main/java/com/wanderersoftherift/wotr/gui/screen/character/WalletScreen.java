package com.wanderersoftherift.wotr.gui.screen.character;

import com.wanderersoftherift.wotr.core.guild.currency.Currency;
import com.wanderersoftherift.wotr.core.guild.currency.Wallet;
import com.wanderersoftherift.wotr.gui.menu.character.WalletMenu;
import com.wanderersoftherift.wotr.gui.widget.ScrollContainerEntry;
import com.wanderersoftherift.wotr.gui.widget.ScrollContainerWidget;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

/**
 * A character screen displaying the player's currencies
 */
public class WalletScreen extends BaseCharacterScreen<WalletMenu> {
    public static final int ICON_SIZE = 16;

    private ScrollContainerWidget<CurrencyDisplay> currencies;

    public WalletScreen(WalletMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();
        Wallet wallet = getMinecraft().player.getData(WotrAttachments.WALLET.get());
        Registry<Currency> registry = minecraft.level.registryAccess().lookupOrThrow(WotrRegistries.Keys.CURRENCIES);
        currencies = new ScrollContainerWidget<>(300, 30, 200, 140,
                registry.stream()
                        .map(registry::wrapAsHolder)
                        .map(currency -> new CurrencyDisplay(font, wallet, currency)
                        )
                        .toList());
        addRenderableWidget(currencies);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        currencies.setHeight(guiGraphics.guiHeight() - 60);
        currencies.setX((guiGraphics.guiWidth() - currencies.getWidth() - MENU_BAR_WIDTH) / 2 + MENU_BAR_WIDTH);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {

    }

    public static class CurrencyDisplay extends AbstractWidget implements ScrollContainerEntry {
        private final Font font;
        private final Wallet wallet;
        private final Holder<Currency> currency;
        private Component displayName;

        public CurrencyDisplay(Font font, Wallet wallet, Holder<Currency> currency) {
            super(0, 0, 200, 0, Component.empty());
            this.font = font;
            this.wallet = wallet;
            this.currency = currency;
            displayName = Currency.getDisplayName(currency);
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
            int amountWidth = font.width(amountString);
            guiGraphics.drawString(font, amountString, getX() + ICON_SIZE + 30 - amountWidth,
                    getY() + ICON_SIZE - font.lineHeight, ChatFormatting.WHITE.getColor(), true);

            int nameWidth = font.width(displayName);
            guiGraphics.drawString(font, displayName, getX() + width - nameWidth - 2,
                    getY() + ICON_SIZE - font.lineHeight, ChatFormatting.WHITE.getColor(), true);
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
            // TODO
        }
    }
}
