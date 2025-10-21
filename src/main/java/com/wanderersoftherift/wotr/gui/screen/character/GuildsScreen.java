package com.wanderersoftherift.wotr.gui.screen.character;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.guild.Guild;
import com.wanderersoftherift.wotr.core.guild.GuildStatus;
import com.wanderersoftherift.wotr.gui.menu.character.GuildMenu;
import com.wanderersoftherift.wotr.gui.widget.ScrollContainerEntry;
import com.wanderersoftherift.wotr.gui.widget.ScrollContainerWidget;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * A character screen displays information on guilds the player is affiliated with
 */
public class GuildsScreen extends BaseCharacterScreen<GuildMenu> {

    private ScrollContainerWidget<GuildDisplay> guildsDisplay;
    private GuildStatus guildStatus;

    public GuildsScreen(GuildMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();
        guildStatus = minecraft.player.getData(WotrAttachments.GUILD_STATUS);
        List<Holder<Guild>> guilds = guildStatus.getGuilds();
        guildsDisplay = new ScrollContainerWidget<>(300, 30, 200, 140,
                guilds.stream().map(guild -> new GuildDisplay(font, guild, guildStatus)).toList());
        addRenderableWidget(guildsDisplay);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guildsDisplay.setHeight(guiGraphics.guiHeight() - 60);
        guildsDisplay.setX((guiGraphics.guiWidth() - guildsDisplay.getWidth() - MENU_BAR_WIDTH) / 2 + MENU_BAR_WIDTH);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {

    }

    private static class GuildDisplay extends AbstractWidget implements ScrollContainerEntry {

        private static final int EMBLEM_SIZE = 32;
        private static final int DISPLAY_HEIGHT = 68;

        private final Font font;
        private final Holder<Guild> guild;
        private final GuildStatus status;

        public GuildDisplay(Font font, Holder<Guild> guild, GuildStatus status) {
            super(0, 0, 300, DISPLAY_HEIGHT, Guild.getDisplayName(guild));
            this.font = font;
            this.guild = guild;
            this.status = status;
        }

        @Override
        public int getHeight(int width) {
            return DISPLAY_HEIGHT;
        }

        @Override
        protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            guiGraphics.blit(RenderType::guiTextured, guild.value().icon32(), getX() + 4, getY() + 4, 0, 0, EMBLEM_SIZE,
                    EMBLEM_SIZE, EMBLEM_SIZE, EMBLEM_SIZE);
            guiGraphics.drawString(font, getMessage(), getX() + EMBLEM_SIZE + 8, getY() + 8,
                    ChatFormatting.WHITE.getColor(), true);
            int rank = status.getRank(guild);
            guiGraphics.drawString(font,
                    Component.translatable(WanderersOfTheRift.translationId("container", "guild.rank"),
                            Guild.getRankTitle(guild, rank)),
                    getX() + EMBLEM_SIZE + 8, getY() + 8 + 2 * font.lineHeight, ChatFormatting.WHITE.getColor(), false);

            if (rank < guild.value().ranks().size()) {
                guiGraphics.drawString(font,
                        Component.translatable(WanderersOfTheRift.translationId("container", "guild.reputation"),
                                status.getReputation(guild), guild.value().getNextRank(rank).reputationRequirement()),
                        getX() + EMBLEM_SIZE + 8, getY() + 8 + 3 * font.lineHeight, ChatFormatting.WHITE.getColor(),
                        false);
            } else {
                guiGraphics.drawString(font,
                        Component.translatable(WanderersOfTheRift.translationId("container", "guild.reputation.max")),
                        getX() + EMBLEM_SIZE + 8, getY() + 8 + 3 * font.lineHeight, ChatFormatting.WHITE.getColor(),
                        false);
            }

        }

        @Override
        protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {
            // TODO
        }
    }
}
