package com.wanderersoftherift.wotr.gui.extension;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.currency.Currency;
import com.wanderersoftherift.wotr.core.currency.Wallet;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ContainerScreenEvent;

/**
 * Injects a currency display into the Inventory GUI
 */
@EventBusSubscriber(modid = WanderersOfTheRift.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
public final class InventoryCurrencyDisplay {

    private static final ResourceLocation CURRENCY_TAB = WanderersOfTheRift.id("textures/gui/container/tab.png");
    private static final ResourceKey<Currency> TRADE_CURRENCY = ResourceKey.create(WotrRegistries.Keys.CURRENCIES,
            WanderersOfTheRift.id("coin"));
    private static final int ICON_SIZE = 16;
    private static final int TAB_WIDTH = 64;
    private static final int TAB_HEIGHT = 19;
    private static final int RIGHT_OFFSET = 3;

    private static Holder<Currency> tradeCurrency;

    private InventoryCurrencyDisplay() {
    }

    @SubscribeEvent
    public static void onClientLoaded(ClientPlayerNetworkEvent.LoggingIn event) {
        tradeCurrency = Minecraft.getInstance().level.registryAccess().holderOrThrow(TRADE_CURRENCY);
    }

    @SubscribeEvent
    public static void onRenderInventoryForeground(ContainerScreenEvent.Render.Foreground event) {
        if (!(event.getContainerScreen() instanceof InventoryScreen) || Minecraft.getInstance().player == null) {
            return;
        }

        GuiGraphics guiGraphics = event.getGuiGraphics();
        Font font = Minecraft.getInstance().font;
        int mouseX = event.getMouseX() - event.getContainerScreen().getGuiLeft();
        int mouseY = event.getMouseY() - event.getContainerScreen().getGuiTop();
        Wallet wallet = Minecraft.getInstance().player.getData(WotrAttachments.WALLET);
        int amount = wallet.get(tradeCurrency);

        int x = event.getContainerScreen().getXSize() - TAB_WIDTH - RIGHT_OFFSET;
        int y = -TAB_HEIGHT;

        renderCurrencyDisplay(guiGraphics, font, x, y, mouseX, mouseY, amount);
    }

    private static void renderCurrencyDisplay(
            GuiGraphics guiGraphics,
            Font font,
            int x,
            int y,
            int mouseX,
            int mouseY,
            int amount) {
        guiGraphics.blit(RenderType::guiTextured, CURRENCY_TAB, x, y, 0, 0, TAB_WIDTH, TAB_HEIGHT, TAB_WIDTH,
                TAB_HEIGHT);
        guiGraphics.blit(RenderType::guiTextured, tradeCurrency.value().icon(), x + TAB_WIDTH - ICON_SIZE - 4, y + 3, 0,
                0, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);

        String amountString = Integer.toString(amount);
        int amountWidth = font.width(amountString);
        guiGraphics.drawString(font, amountString, x + TAB_WIDTH - ICON_SIZE - 6 - amountWidth, -font.lineHeight,
                ChatFormatting.BLACK.getColor(), false);

        if (mouseX >= x + TAB_WIDTH - ICON_SIZE - 4 && mouseX < x + TAB_WIDTH - 4 && mouseY >= y + 3 && mouseY < 0) {
            guiGraphics.renderTooltip(font, Currency.getDisplayName(tradeCurrency), mouseX, mouseY);
        }
    }
}
