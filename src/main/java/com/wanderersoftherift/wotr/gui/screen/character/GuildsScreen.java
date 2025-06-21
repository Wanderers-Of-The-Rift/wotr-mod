package com.wanderersoftherift.wotr.gui.screen.character;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.guild.GuildInfo;
import com.wanderersoftherift.wotr.gui.menu.character.GuildMenu;
import com.wanderersoftherift.wotr.gui.widget.ScrollContainerEntry;
import com.wanderersoftherift.wotr.gui.widget.ScrollContainerWidget;
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

public class GuildsScreen extends BaseCharacterScreen<GuildMenu> {

    private ScrollContainerWidget<GuildDisplay> guilds;

    public GuildsScreen(GuildMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();
        Registry<GuildInfo> registry = minecraft.level.registryAccess().lookupOrThrow(WotrRegistries.Keys.GUILDS);
        guilds = new ScrollContainerWidget<>(300, 30, 300, 140,
                registry.stream()
                        .map(registry::wrapAsHolder)
                        .map(guild -> new GuildDisplay(font, guild)
                        )
                        .toList());
        addRenderableWidget(guilds);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guilds.setHeight(guiGraphics.guiHeight() - 60);
        guilds.setX((guiGraphics.guiWidth() - guilds.getWidth() - MENU_BAR_WIDTH) / 2 + MENU_BAR_WIDTH);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {

    }

    private static class GuildDisplay extends AbstractWidget implements ScrollContainerEntry {

        private static final int EMBLEM_SIZE = 64;

        private Font font;
        private Holder<GuildInfo> guild;

        public GuildDisplay(Font font, Holder<GuildInfo> guild) {
            super(0, 0, 300, 68, GuildInfo.getDisplayName(guild));
            this.font = font;
            this.guild = guild;
        }

        @Override
        public int getHeight(int width) {
            return 68;
        }

        @Override
        protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            guiGraphics.blit(RenderType::guiTextured, guild.value().emblem(), getX() + 4, getY() + 4, 0, 0, EMBLEM_SIZE,
                    EMBLEM_SIZE, EMBLEM_SIZE, EMBLEM_SIZE);

            guiGraphics.drawString(font, getMessage(), getX() + EMBLEM_SIZE + 8, getY() + 8,
                    ChatFormatting.WHITE.getColor(), true);
            guiGraphics.drawString(font,
                    Component.translatable(WanderersOfTheRift.translationId("container", "guild.rank"),
                            GuildInfo.getRankTitle(guild, 0)),
                    getX() + EMBLEM_SIZE + 8, getY() + 8 + 2 * font.lineHeight, ChatFormatting.WHITE.getColor(), false);
            guiGraphics.drawString(font,
                    Component.translatable(WanderersOfTheRift.translationId("container", "guild.reputation"), 0, 1000),
                    getX() + EMBLEM_SIZE + 8, getY() + 8 + 3 * font.lineHeight, ChatFormatting.WHITE.getColor(), false);

        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

        }
    }
}
