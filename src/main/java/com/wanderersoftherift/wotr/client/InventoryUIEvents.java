package com.wanderersoftherift.wotr.client;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.guild.currency.Currency;
import com.wanderersoftherift.wotr.core.guild.currency.Wallet;
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

@EventBusSubscriber(modid = WanderersOfTheRift.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
public final class InventoryUIEvents {

    private static final ResourceLocation CURRENCY_TAB = WanderersOfTheRift.id("textures/gui/container/tab.png");
    private static final ResourceKey<Currency> TRADE_CURRENCY = ResourceKey.create(WotrRegistries.Keys.CURRENCIES,
            WanderersOfTheRift.id("coin"));
    private static final int ICON_SIZE = 16;
    private static final int TAB_WIDTH = 64;
    private static final int TAB_HEIGHT = 19;

    private static Holder<Currency> tradeCurrency;

    private InventoryUIEvents() {
    }

    @SubscribeEvent
    public static void onClientLoaded(ClientPlayerNetworkEvent.LoggingIn event) {
        tradeCurrency = Minecraft.getInstance().level.registryAccess().holderOrThrow(TRADE_CURRENCY);
    }

    @SubscribeEvent
    public static void onRenderInventoryForeground(ContainerScreenEvent.Render.Foreground event) {
        if (!(event.getContainerScreen() instanceof InventoryScreen)) {
            return;
        }
        GuiGraphics guiGraphics = event.getGuiGraphics();
        if (Minecraft.getInstance().player == null) {
            return;
        }
        Wallet wallet = Minecraft.getInstance().player.getData(WotrAttachments.WALLET);
        int amount = wallet.get(tradeCurrency);

        int x = event.getContainerScreen().getXSize() - TAB_WIDTH - 3;
        int y = -TAB_HEIGHT;

        Font font = Minecraft.getInstance().font;

        guiGraphics.blit(RenderType::guiTextured, CURRENCY_TAB, x, y, 0, 0, TAB_WIDTH, TAB_HEIGHT, TAB_WIDTH,
                TAB_HEIGHT);
        guiGraphics.blit(RenderType::guiTextured, tradeCurrency.value().icon(), x + TAB_WIDTH - ICON_SIZE - 4, y + 3, 0,
                0, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);

        String amountString = Integer.toString(amount);
        int amountWidth = font.width(amountString);
        guiGraphics.drawString(font, amountString, x + TAB_WIDTH - ICON_SIZE - 6 - amountWidth, -font.lineHeight,
                ChatFormatting.BLACK.getColor(), false);

        int mouseX = event.getMouseX() - event.getContainerScreen().getGuiLeft();
        int mouseY = event.getMouseY() - event.getContainerScreen().getGuiTop();
        if (mouseX >= x + TAB_WIDTH - ICON_SIZE - 4 && mouseX < x + TAB_WIDTH - 4 && mouseY >= y + 3 && mouseY < 0) {
            guiGraphics.renderTooltip(font, Currency.getDisplayName(tradeCurrency), mouseX, mouseY);
        }
    }
}
